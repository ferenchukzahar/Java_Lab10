import java.lang.reflect.Field;
import java.util.Scanner;

public class ReflectionStringDemo {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // 1. Рядок, ініціалізований як строковий літерал
        String literalString = "Початковий рядок (літерал)";

        // 2. Рядок, введений з клавіатури
        System.out.print("Введіть початковий рядок з клавіатури: ");
        String keyboardString = scanner.nextLine();

        System.out.print("Введіть нове значення для заміни літералу: ");
        String newLiteralValue = scanner.nextLine();

        System.out.print("Введіть нове значення для заміни введеного рядка: ");
        String newKeyboardValue = scanner.nextLine();

        System.out.println("\n=== ДО ЗМІНИ ЧЕРЕЗ РЕФЛЕКСІЮ ===");
        System.out.println("1) Літерал:            \"" + literalString + "\"");
        System.out.println("2) З клавіатури:       \"" + keyboardString + "\"");

        try {
            modifyString(literalString, newLiteralValue);
            modifyString(keyboardString, newKeyboardValue);
        } catch (Exception e) {
            System.err.println("Помилка рефлексії: " + e);
            return;
        }

        System.out.println("\n=== ПІСЛЯ ЗМІНИ ЧЕРЕЗ РЕФЛЕКСІЮ ===");
        System.out.println("1) Літерал:            \"" + literalString + "\"");
        System.out.println("2) З клавіатури:       \"" + keyboardString + "\"");
        System.out.println("Перевірка String Pool: \"" + "Початковий рядок (літерал)" + "\"");

        scanner.close();
    }

    private static void modifyString(String target, String replacement) throws Exception {
        Field valueField = String.class.getDeclaredField("value");
        valueField.setAccessible(true);

        if (valueField.getType() == char[].class) {
            // Для Java 8 та старіших версій
            valueField.set(target, replacement.toCharArray());
        } else if (valueField.getType() == byte[].class) {
            // Для Java 9+ (Compact Strings: byte[] + coder)
            Field coderField = String.class.getDeclaredField("coder");
            coderField.setAccessible(true);

            byte replacementCoder = coderField.getByte(replacement);
            byte[] replacementBytes = (byte[]) valueField.get(replacement);

            valueField.set(target, replacementBytes.clone());
            coderField.setByte(target, replacementCoder);
        }
    }
}
