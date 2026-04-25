package com.vanapp.service;

import com.google.ortools.Loader;
import com.google.ortools.constraintsolver.*;
import com.google.protobuf.Duration;
import com.vanapp.model.Usuario;
import com.vanapp.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RotaService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    static {
        Loader.loadNativeLibraries();
    }

    // Calcula distância em metros entre dois pontos (fórmula de Haversine)
    private double calcularDistancia(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371000;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    public List<Usuario> otimizarRota(Long motoristaId) {
        // Busca todos os passageiros
        List<Usuario> passageiros = usuarioRepository.findAll()
                .stream()
                .filter(u -> u.getTipo().equals("PASSAGEIRO"))
                .toList();

        if (passageiros.isEmpty()) {
            throw new RuntimeException("Nenhum passageiro cadastrado");
        }

        // Busca motorista
        Usuario motorista = usuarioRepository.findById(motoristaId)
                .orElseThrow(() -> new RuntimeException("Motorista não encontrado"));

        // Monta lista de pontos: motorista + passageiros
        int n = passageiros.size() + 1;
        double[] lats = new double[n];
        double[] lons = new double[n];

        lats[0] = motorista.getLatitude();
        lons[0] = motorista.getLongitude();

        for (int i = 0; i < passageiros.size(); i++) {
            lats[i + 1] = passageiros.get(i).getLatitude();
            lons[i + 1] = passageiros.get(i).getLongitude();
        }

        // Monta matriz de distâncias
        long[][] distancias = new long[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                distancias[i][j] = (long) calcularDistancia(lats[i], lons[i], lats[j], lons[j]);
            }
        }

        // OR-Tools
        RoutingIndexManager manager = new RoutingIndexManager(n, 1, 0);
        RoutingModel routing = new RoutingModel(manager);

        int transitCallbackIndex = routing.registerTransitCallback((from, to) -> {
            int fromNode = manager.indexToNode(from);
            int toNode = manager.indexToNode(to);
            return distancias[fromNode][toNode];
        });

        routing.setArcCostEvaluatorOfAllVehicles(transitCallbackIndex);

        RoutingSearchParameters searchParameters = main.defaultRoutingSearchParameters()
                .toBuilder()
                .setFirstSolutionStrategy(FirstSolutionStrategy.Value.PATH_CHEAPEST_ARC)
                .setTimeLimit(Duration.newBuilder().setSeconds(5).build())
                .build();

        Assignment solution = routing.solveWithParameters(searchParameters);

        if (solution == null) {
            throw new RuntimeException("Não foi possível otimizar a rota");
        }

        // Monta resultado na ordem otimizada
        List<Usuario> rotaOtimizada = new java.util.ArrayList<>();
        long index = routing.start(0);
        index = solution.value(routing.nextVar(index)); // pula o ponto inicial (motorista)

        while (!routing.isEnd(index)) {
            int nodeIndex = manager.indexToNode(index);
            rotaOtimizada.add(passageiros.get(nodeIndex - 1));
            index = solution.value(routing.nextVar(index));
        }

        return rotaOtimizada;
    }
}