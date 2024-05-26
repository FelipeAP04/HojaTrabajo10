package uvg.edu.gt;

import java.io.*;
import java.util.*;

public class CovidResponseCenter {
    public static final int INF = Integer.MAX_VALUE / 2;
    public Map<String, Integer> cityIndex = new HashMap<>();
    public List<String> cities = new ArrayList<>();
    public int[][] dist;
    public int[][] next;

    public static void main(String[] args) throws IOException {
        CovidResponseCenter center = new CovidResponseCenter();
        Scanner scanner = new Scanner(System.in);
        center.loadGraph("guategrafo.txt");
        center.floydWarshall();

        while (true) {
            System.out.println("1. Consultar ruta más corta");
            System.out.println("2. Indicar ciudad centro del grafo");
            System.out.println("3. Modificar grafo");
            System.out.println("4. Finalizar programa");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    System.out.print("Ciudad origen: ");
                    String start = scanner.nextLine();
                    System.out.print("Ciudad destino: ");
                    String end = scanner.nextLine();
                    center.shortestPath(start, end);
                    break;
                case 2:
                    String centerCity = center.graphCenter();
                    System.out.println("La ciudad centro del grafo es: " + centerCity);
                    break;
                case 3:
                    center.modifyGraph(scanner);
                    center.floydWarshall();
                    break;
                case 4:
                    System.exit(0);
                    break;
                default:
                    System.out.println("Opción no válida");
            }
        }
    }

    public void loadGraph(String filename) throws IOException {
        InputStream is = getClass().getClassLoader().getResourceAsStream(filename);
        if (is == null) {
            throw new FileNotFoundException("Archivo " + filename + " no encontrado en src/main/resources");
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line;
        while ((line = br.readLine()) != null) {
            String[] parts = line.split(" ");
            String city1 = parts[0];
            String city2 = parts[1];
            int km = Integer.parseInt(parts[2]);

            if (!cityIndex.containsKey(city1)) {
                cityIndex.put(city1, cities.size());
                cities.add(city1);
            }
            if (!cityIndex.containsKey(city2)) {
                cityIndex.put(city2, cities.size());
                cities.add(city2);
            }
        }
        br.close();

        int n = cities.size();
        dist = new int[n][n];
        next = new int[n][n];

        for (int i = 0; i < n; i++) {
            Arrays.fill(dist[i], INF);
            dist[i][i] = 0;
            for (int j = 0; j < n; j++) {
                next[i][j] = j;
            }
        }

        is = getClass().getClassLoader().getResourceAsStream(filename);
        if (is == null) {
            throw new FileNotFoundException("Archivo " + filename + " no encontrado en src/main/resources");
        }
        br = new BufferedReader(new InputStreamReader(is));
        while ((line = br.readLine()) != null) {
            String[] parts = line.split(" ");
            String city1 = parts[0];
            String city2 = parts[1];
            int km = Integer.parseInt(parts[2]);

            int u = cityIndex.get(city1);
            int v = cityIndex.get(city2);
            dist[u][v] = km;
        }
        br.close();
    }

    public void floydWarshall() {
        int n = cities.size();
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (dist[i][j] > dist[i][k] + dist[k][j]) {
                        dist[i][j] = dist[i][k] + dist[k][j];
                        next[i][j] = next[i][k];
                    }
                }
            }
        }
    }

    public void shortestPath(String start, String end) {
        if (!cityIndex.containsKey(start) || !cityIndex.containsKey(end)) {
            System.out.println("Una o ambas ciudades no existen en el grafo.");
            return;
        }

        int u = cityIndex.get(start);
        int v = cityIndex.get(end);
        if (dist[u][v] == INF) {
            System.out.println("No hay ruta disponible entre " + start + " y " + end);
            return;
        }

        System.out.println("La distancia más corta es: " + dist[u][v] + " KM");
        List<String> path = new ArrayList<>();
        for (int at = u; at != v; at = next[at][v]) {
            path.add(cities.get(at));
        }
        path.add(cities.get(v));

        System.out.println("Ruta: " + String.join(" -> ", path));
    }

    public String graphCenter() {
        int n = cities.size();
        int minMaxDist = INF;
        String centerCity = null;

        for (int i = 0; i < n; i++) {
            int maxDist = 0;
            for (int j = 0; j < n; j++) {
                if (dist[i][j] < INF) {
                    maxDist = Math.max(maxDist, dist[i][j]);
                }
            }
            if (maxDist < minMaxDist) {
                minMaxDist = maxDist;
                centerCity = cities.get(i);
            }
        }

        return centerCity;
    }

    public void modifyGraph(Scanner scanner) {
        System.out.println("a) Interrupción de tráfico");
        System.out.println("b) Establecer nueva conexión");
        String option = scanner.nextLine();

        switch (option) {
            case "a":
                System.out.print("Ciudad 1: ");
                String city1 = scanner.nextLine();
                System.out.print("Ciudad 2: ");
                String city2 = scanner.nextLine();

                if (!cityIndex.containsKey(city1) || !cityIndex.containsKey(city2)) {
                    System.out.println("Una o ambas ciudades no existen en el grafo.");
                    return;
                }

                int u = cityIndex.get(city1);
                int v = cityIndex.get(city2);
                dist[u][v] = INF;
                break;

            case "b":
                System.out.print("Ciudad 1: ");
                String cityA = scanner.nextLine();
                System.out.print("Ciudad 2: ");
                String cityB = scanner.nextLine();
                System.out.print("Distancia en KM: ");
                int km = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                if (!cityIndex.containsKey(cityA)) {
                    cityIndex.put(cityA, cities.size());
                    cities.add(cityA);
                }
                if (!cityIndex.containsKey(cityB)) {
                    cityIndex.put(cityB, cities.size());
                    cities.add(cityB);
                }

                int x = cityIndex.get(cityA);
                int y = cityIndex.get(cityB);
                dist[x][y] = km;
                break;

            default:
                System.out.println("Opción no válida");
                break;
        }
    }
}
