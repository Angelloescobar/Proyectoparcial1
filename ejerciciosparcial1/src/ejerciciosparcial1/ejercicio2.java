package ejerciciosparcial1;

import java.util.Arrays;

public class ejercicio2 {

    public static int[] encontrarSegundoMenorYSegundoMayor(int[] numeros) {
        int menor = Integer.MAX_VALUE;
        int segundoMenor = Integer.MAX_VALUE;
        int mayor = Integer.MIN_VALUE;
        int segundoMayor = Integer.MIN_VALUE;

        for (int numero : numeros) {
            if (numero < menor) {
                segundoMenor = menor;
                menor = numero;
            } else if (numero > menor && numero < segundoMenor) {
                segundoMenor = numero;
            }

            if (numero > mayor) {
                segundoMayor = mayor;
                mayor = numero;
            } else if (numero < mayor && numero > segundoMayor) {
                segundoMayor = numero;
            }
        }

        return new int[]{segundoMenor, segundoMayor};
    }

    public static void main(String[] args) {
        int[] arreglo = {7, 2, 9, 4, 1, 8};

        int[] resultado = encontrarSegundoMenorYSegundoMayor(arreglo);

        System.out.println("Arreglo original: " + Arrays.toString(arreglo));
        System.out.println("Resultado: " + Arrays.toString(resultado));
    }
}






