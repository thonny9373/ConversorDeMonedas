import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import org.json.JSONObject;

public class CurrencyConverter {
    private static final String API_KEY = "3a30fb4a343b51f0c291eb50";
    private static final String BASE_URL = "https://v6.exchangerate-api.com/v6/" + API_KEY + "/latest/";

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            int option;
            System.out.println("Bienvenidos al Conversor de Monedas");

            do {
                System.out.println("Seleccione una opción:");
                System.out.println("1. Dólar a Peso Argentino");
                System.out.println("2. Peso Argentino a Dólar");
                System.out.println("3. Dólar a Real Brasileño");
                System.out.println("4. Real Brasileño a Dólar");
                System.out.println("5. Dólar a Peso Colombiano");
                System.out.println("6. Peso Colombiano a Dólar");
                System.out.println("7. Convertir otra moneda");
                System.out.println("8. Salir");

                while (!scanner.hasNextInt()) {
                    System.out.println("Por favor, ingrese un número válido.");
                    scanner.next();
                }
                option = scanner.nextInt();

                switch (option) {
                    case 1 -> convertCurrency("USD", "ARS");
                    case 2 -> convertCurrency("ARS", "USD");
                    case 3 -> convertCurrency("USD", "BRL");
                    case 4 -> convertCurrency("BRL", "USD");
                    case 5 -> convertCurrency("USD", "COP");
                    case 6 -> convertCurrency("COP", "USD");
                    case 7 -> {
                        System.out.println("Ingrese la moneda de origen (código ISO, e.g., USD): ");
                        String from = scanner.next().toUpperCase();
                        System.out.println("Ingrese la moneda de destino (código ISO, e.g., EUR): ");
                        String to = scanner.next().toUpperCase();
                        convertCurrency(from, to);
                    }
                    case 8 -> System.out.println("Gracias por usar el Conversor de Monedas. ¡Adiós!");
                    default -> System.out.println("Opción no válida. Por favor, intente de nuevo.");
                }
            } while (option != 8);
        }
    }

    private static void convertCurrency(String from, String to) {
        try {
            // Crear la URL para la API
            URL url = new URL(BASE_URL + from);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            // Verificar si la conexión es exitosa
            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                System.out.println("Error al conectar con la API: " + responseCode);
                return;
            }

            // Leer los datos de la API
            try (Scanner scanner = new Scanner(url.openStream())) {
                StringBuilder inline = new StringBuilder();
                while (scanner.hasNext()) {
                    inline.append(scanner.nextLine());
                }

                // Parsear la respuesta JSON
                JSONObject jsonObject = new JSONObject(inline.toString());
                if (!jsonObject.has("conversion_rates") || !jsonObject.getJSONObject("conversion_rates").has(to)) {
                    System.out.println("La conversión no está disponible para " + from + " a " + to);
                    return;
                }

                double exchangeRate = jsonObject.getJSONObject("conversion_rates").getDouble(to);

                // Solicitar la cantidad a convertir
                Scanner inputScanner = new Scanner(System.in);
                System.out.println("Ingrese la cantidad en " + from + ": ");
                while (!inputScanner.hasNextDouble()) {
                    System.out.println("Por favor, ingrese un número válido.");
                    inputScanner.next();
                }
                double amount = inputScanner.nextDouble();

                // Realizar la conversión
                double convertedAmount = amount * exchangeRate;
                System.out.printf("%.2f %s equivalen a %.2f %s%n", amount, from, convertedAmount, to);
            }
        } catch (Exception e) {
            System.out.println("Ocurrió un error: " + e.getMessage());
        }
    }
}
