package principal;
import java.io.*;
import java.util.*;
public class MzclaMultiplePrueba {
	
	    static final int N = 6;
	    static final int N2 = N/2;
	    static File f0;
	    static File[] f = new File[N];
	    static final int NumReg = 149;
	    static final int TOPE = 999;

	    // Writer para volcar salida en fichero
	    static BufferedWriter out;

	    public static void main(String[] a) {
	        String basePath = "C:\\Users\\tamay\\Desktop\\";
	        String origenName = basePath + "ArchivoOrigen.dat";
	        String outputName = basePath + "FicheroOrdenado.txt";

	        try {
	            // Inicializar writer de salida
	            out = new BufferedWriter(new FileWriter(outputName));

	            // Archivos temporales de mezcla
	            String[] nomf = {"ar1.dat","ar2.dat","ar3.dat","ar4.dat","ar5.dat","ar6.dat"};
	            f0 = new File(origenName);
	            for (int i = 0; i < N; i++) {
	                f[i] = new File(basePath + nomf[i]);
	            }

	            // Generar ArchivoOrigen con datos aleatorios
	            try (DataOutputStream flujo = new DataOutputStream(
	                    new BufferedOutputStream(new FileOutputStream(f0)))) {
	                for (int i = 1; i <= NumReg; i++) {
	                    flujo.writeInt((int)(1 + TOPE * Math.random()));
	                }
	            }

	            // Escribir contenido original (opcional)
	            escribir(f0);
	            out.newLine();
	            escribir(f0);

	            // Ejecutar algoritmo de mezcla equilibrada múltiple (inmutable)
	            mezclaEqMple();

	            // Cerrar writer
	            out.close();
	            System.out.println("¡Salida escrita en: " + outputName);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }

	    public static void mezclaEqMple() {
	        int i, j, k1, t;
	        int anterior;
	        int[] c = new int[N];
	        int[] cd = new int[N];
	        int[] r = new int[N2];
	        Object[] flujos = new Object[N];
	        DataInputStream flujoEntradaActual;
	        DataOutputStream flujoSalidaActual;
	        boolean[] actvs = new boolean[N2];

	        try {
	            t = distribuir();
	            for (i = 0; i < N; i++) c[i] = i;

	            do {
	                k1 = (t < N2) ? t : N2;
	                for (i = 0; i < k1; i++) {
	                    flujos[c[i]] = new DataInputStream(
	                        new BufferedInputStream(new FileInputStream(f[c[i]])));
	                    cd[i] = c[i];
	                }
	                j = N2;
	                t = 0;
	                for (i = j; i < N; i++) {
	                    flujos[c[i]] = new DataOutputStream(
	                        new BufferedOutputStream(new FileOutputStream(f[c[i]])));
	                }
	                for (int n = 0; n < k1; n++) {
	                    flujoEntradaActual = (DataInputStream) flujos[cd[n]];
	                    r[n] = flujoEntradaActual.readInt();
	                }

	                while (k1 > 0) {
	                    t++;
	                    for (i = 0; i < k1; i++) actvs[i] = true;
	                    flujoSalidaActual = (DataOutputStream) flujos[c[j]];

	                    while (!finDeTramos(actvs, k1)) {
	                        int n = minimo(r, actvs, k1);
	                        flujoEntradaActual = (DataInputStream) flujos[cd[n]];
	                        flujoSalidaActual.writeInt(r[n]);
	                        anterior = r[n];
	                        try {
	                            r[n] = flujoEntradaActual.readInt();
	                            if (anterior > r[n]) actvs[n] = false;
	                        } catch (EOFException eof) {
	                            k1--;
	                            flujoEntradaActual.close();
	                            cd[n] = cd[k1];
	                            r[n] = r[k1];
	                            actvs[n] = actvs[k1];
	                            actvs[k1] = false;
	                        }
	                    }
	                    j = (j < N - 1) ? j + 1 : N2;
	                }
	                for (i = N2; i < N; i++) {
	                    flujoSalidaActual = (DataOutputStream) flujos[c[i]];
	                    flujoSalidaActual.close();
	                }
	                for (i = 0; i < N2; i++) {
	                    int a = c[i];
	                    c[i] = c[i + N2];
	                    c[i + N2] = a;
	                }
	            } while (t > 1);

	            // Tras mezcla final, escribir el archivo ordenado
	            escribir(f[c[0]]);
	        } catch (IOException er) {
	            er.printStackTrace();
	        }
	    }

	    private static int distribuir() throws IOException {
	        DataInputStream flujo = new DataInputStream(
	            new BufferedInputStream(new FileInputStream(f0)));
	        DataOutputStream[] flujoSalida = new DataOutputStream[N2];
	        for (int j = 0; j < N2; j++) {
	            flujoSalida[j] = new DataOutputStream(
	                new BufferedOutputStream(new FileOutputStream(f[j])));
	        }
	        int anterior = -TOPE;
	        int clave;
	        int j = 0;
	        int nt = 0;
	        try {
	            while (true) {
	                clave = flujo.readInt();
	                while (anterior <= clave) {
	                    flujoSalida[j].writeInt(clave);
	                    anterior = clave;
	                    clave = flujo.readInt();
	                }
	                nt++;
	                j = (j < N2 - 1) ? j + 1 : 0;
	                flujoSalida[j].writeInt(clave);
	                anterior = clave;
	            }
	        } catch (EOFException eof) {
	            nt++;
	            flujo.close();
	            for (int k = 0; k < N2; k++) flujoSalida[k].close();
	            return nt;
	        }
	    }

	    private static int minimo(int[] r, boolean[] activo, int n) {
	        int m = TOPE + 1;
	        int indice = 0;
	        for (int i = 0; i < n; i++) {
	            if (activo[i] && r[i] < m) {
	                m = r[i];
	                indice = i;
	            }
	        }
	        return indice;
	    }

	    private static boolean finDeTramos(boolean[] activo, int n) {
	        for (int i = 0; i < n; i++) {
	            if (activo[i]) return false;
	        }
	        return true;
	    }

	    static void escribir(File file) {
	        try (DataInputStream flujo = new DataInputStream(
	                new BufferedInputStream(new FileInputStream(file)))) {
	            int k = 0;
	            while (true) {
	                int val = flujo.readInt();
	                // Escribir en fichero en lugar de consola
	                out.write(val + ((k % 19 == 18) ? "\n" : " "));
	                k++;
	            }
	        } catch (EOFException eof) {
	            // Fin de archivo
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	}


//dividir el archivo en ficheros separados temporalmente de un tamaño que si quepa en memoria para despues ordenarlos y 
//fusionar esos ficheros,finalmente imprimir el resultado final en un fichero grande 

