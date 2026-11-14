import java.io.IOException;
import java.util.*;
import java.util.logging.*;

public class InternationalLoggerApp {
    private static final Logger logger = Logger.getLogger(InternationalLoggerApp.class.getName());
    private static ResourceBundle bundle;
    private static Locale currentLocale;

    public static void main(String[] args) {
        setupLogger();
        setLocale(new Locale("uk"));

        Scanner scanner = new Scanner(System.in);
        Map<String, String> dictionary = new HashMap<>();
        dictionary.put("hello", "привіт");
        dictionary.put("world", "світ");
        dictionary.put("java", "джава");

        logger.info("Додаток запущено. Початковий розмір словника: " + dictionary.size());
        logger.fine("Повідомлення рівня FINE не потрапить у логи через поріг Level.INFO");

        boolean running = true;
        while (running) {
            System.out.println("\n" + bundle.getString("app.title"));
            System.out.println(bundle.getString("menu.process"));
            System.out.println(bundle.getString("menu.add"));
            System.out.println(bundle.getString("menu.view"));
            System.out.println(bundle.getString("menu.language"));
            System.out.println(bundle.getString("menu.exit"));
            System.out.print(bundle.getString("menu.prompt") + " ");

            String choice = scanner.nextLine().trim();
            logger.fine("Введено команду меню: " + choice);

            switch (choice) {
                case "1":
                    System.out.print(bundle.getString("input.phrase") + " ");
                    String phrase = scanner.nextLine().trim();
                    if (phrase.isEmpty()) {
                        logger.warning("Спроба перекладу порожнього рядка!");
                        System.out.println(bundle.getString("msg.empty_error"));
                    } else {
                        String translated = translate(phrase, dictionary);
                        System.out.println(bundle.getString("output.result") + " " + translated);
                        logger.info("Виконано переклад фрази: '" + phrase + "' -> '" + translated + "'");
                    }
                    break;

                case "2":
                    System.out.print(bundle.getString("input.key") + " ");
                    String key = scanner.nextLine().trim().toLowerCase();
                    System.out.print(bundle.getString("input.value") + " ");
                    String val = scanner.nextLine().trim().toLowerCase();

                    if (key.isEmpty() || val.isEmpty()) {
                        logger.severe("Помилка додавання запису: порожній ключ або значення!");
                        System.out.println(bundle.getString("msg.empty_error"));
                    } else {
                        dictionary.put(key, val);
                        System.out.println(bundle.getString("msg.added"));
                        logger.info("Додано нову пару слів: " + key + " -> " + val);
                    }
                    break;

                case "3":
                    System.out.println("\n" + bundle.getString("msg.list_header"));
                    dictionary.forEach((k, v) -> System.out.println(k + " -> " + v));
                    logger.info("Переглянуто список записів (" + dictionary.size() + " шт.)");
                    break;

                case "4":
                    changeLanguage(scanner);
                    break;

                case "0":
                    running = false;
                    System.out.println(bundle.getString("msg.exit"));
                    logger.info("Користувач завершив роботу програми.");
                    break;

                default:
                    System.out.println(bundle.getString("msg.invalid_choice"));
                    logger.warning("Введено невідомий пункт меню: '" + choice + "'");
            }
        }
        scanner.close();
    }

    private static void setupLogger() {
        try {
            logger.setUseParentHandlers(false);
            logger.setLevel(Level.INFO);

            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setLevel(Level.WARNING);
            consoleHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(consoleHandler);

            FileHandler fileHandler = new FileHandler("app_execution.log", true);
            fileHandler.setEncoding("UTF-8");
            fileHandler.setLevel(Level.INFO);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);

        } catch (IOException e) {
            System.err.println("Не вдалося ініціалізувати файловий логер: " + e.getMessage());
        }
    }

    private static void changeLanguage(Scanner scanner) {
        System.out.print(bundle.getString("lang.select") + " ");
        String langChoice = scanner.nextLine().trim();
        if ("1".equals(langChoice)) {
            setLocale(new Locale("uk"));
            System.out.println(bundle.getString("lang.changed"));
            logger.info("Мову інтерфейсу змінено на UK");
        } else if ("2".equals(langChoice)) {
            setLocale(Locale.ENGLISH);
            System.out.println(bundle.getString("lang.changed"));
            logger.info("Interface language changed to EN");
        } else {
            System.out.println(bundle.getString("lang.invalid"));
            logger.warning("Невдала спроба зміни мови: введено '" + langChoice + "'");
        }
    }

    private static void setLocale(Locale locale) {
        currentLocale = locale;
        bundle = ResourceBundle.getBundle("location.messages", currentLocale);
    }

    private static String translate(String phrase, Map<String, String> dictionary) {
        String[] words = phrase.split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String w : words) {
            String clean = w.toLowerCase().replaceAll("[^a-zA-Zа-яА-ЯіІїЇєЄ]", "");
            String translated = dictionary.getOrDefault(clean, "[" + w + "]");
            sb.append(translated).append(" ");
        }
        return sb.toString().trim();
    }
}
