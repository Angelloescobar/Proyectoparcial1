package ejerciciosparcial1;

public class ejercicio1 {

    public static int calcularPuntaje(int[] numeros) {
        int puntajeTotal = 0;

        for (int numero : numeros) {
            if (numero == 5) {
                puntajeTotal += 5;
            } else if (numero % 2 == 0) {
                puntajeTotal += 1;
            } else {
                puntajeTotal += 3;
            }
        }

        return puntajeTotal;
    }

    public static void main(String[] args) {
        int[] arreglo1 = {1, 2, 3, 4, 5};
        int[] arreglo2 = {17, 19, 21};
        int[] arreglo3 = {5, 5, 5};

        System.out.println("Arreglo 1: " + calcularPuntaje(arreglo1));
        System.out.println("Arreglo 2: " + calcularPuntaje(arreglo2));
        System.out.println("Arreglo 3: " + calcularPuntaje(arreglo3));
    }
}