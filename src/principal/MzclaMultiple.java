package principal;
import java.io.*;
import java.util.*;
public class MzclaMultiple {
	

	    static final int N = 8;
	    static final int N2 = N / 2;
	    static final int NumReg = 259999999;
	    static final int TOPE = 999999999;
	    static File f0;
	    static File[] f = new File[N];
	    static int ultimoNumTramos;
	    
	    // Writer para volcar salida en fichero
	    static BufferedWriter out;

	    public static void main(String[] args) {
	        String basePath = "C:\\Users\\tamay\\Desktop\\";
	        String origenName = basePath + "ArchivoOrigen.dat";
	        String outputName = basePath + "FicheroOrdenado.txt";

	        try {
	            // Inicializar writer de salida
	            out = new BufferedWriter(new FileWriter(outputName));

	            // Generar ArchivoOrigen con datos aleatorios 
	            try (DataOutputStream flujo = new DataOutputStream(
	                    new BufferedOutputStream(new FileOutputStream(origenName), 64 * 1024))) {
	                for (int i = 1; i <= NumReg; i++) {
	                    flujo.writeInt(1 + (int)(TOPE * Math.random()));
	                }
	            }

	            // Archivos temporales de mezcla
				String[] nomf = { "ar1.dat", "ar2.dat", "ar3.dat", "ar4.dat", "ar5.dat", "ar6.dat", "ar7.dat","ar8.dat" };
	            f0 = new File(origenName);
	            for (int i = 0; i < N; i++) {
	                f[i] = new File(basePath + nomf[i]);
	            }

	            // 1. Escribir datos desordenados
	            escribir(f0);

	            // Marcas y número de tramos
	            out.newLine();
	            out.write("***Fin del archivo***");
	            out.newLine();
	            out.newLine();
	            out.write("***Número de tramos: " + distribuir() + "***");
	            out.newLine();
	            out.write("***Archivo Ordenado***");
	            out.newLine();

	            // 2. Mezcla y obtención del fichero ordenado
	            File sorted = mezclaEqMple();

	            // 3. Escribir datos ordenados
	            escribir(sorted);

	            // Cerrar writer
	            out.close();
	            System.out.println("¡Salida escrita en: " + outputName);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }

	    /**
	     * Distribuye runs desde f0 hacia los N2 primeros ficheros.
	     * @return número total de runs
	     */
	    private static int distribuir() throws IOException {
	    	int anterior, j, nt , clave;
	    	
	        DataInputStream flujo = new DataInputStream(
	            new BufferedInputStream(new FileInputStream(f0), 64 * 1024));
	        DataOutputStream[] flujoSalida = new DataOutputStream[N2];
	        for ( j = 0; j < N2; j++) {
	            flujoSalida[j] = new DataOutputStream(
	                new BufferedOutputStream(new FileOutputStream(f[j]), 64 * 1024));
	        }
	         anterior = -TOPE;
	        clave = anterior+1;
	         j = 0;
	         nt = 0;
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
	            for (DataOutputStream ds : flujoSalida) ds.close();
	            ultimoNumTramos = nt;
	            return nt;
	        }
	    }

	    /**
	     * Realiza la mezcla equilibrada múltiple y devuelve el fichero final ordenado.
	     */
	    public static File mezclaEqMple() {
	        int i, j, k1, t;
	        int anterior;
	        int[] c = new int[N];
	        int[] cd = new int[N];
	        int[] r = new int[N2];
	        Object[] flujos = new Object[N];
	        DataInputStream flujoEntradaActual = null;
	        DataOutputStream flujoSalidaActual = null;
	        boolean[] actvs = new boolean[N2];
	        try {
	            t = distribuir();
	            for (i = 0; i < N; i++) c[i] = i;
	            // bucle hasta que solo quede un tramo
	            do {
	                k1 = (t < N2) ? t : N2;
	                // abrir flujos de entrada
	                for (i = 0; i < k1; i++) {
	                    flujos[c[i]] = new DataInputStream(
	                        new BufferedInputStream(new FileInputStream(f[c[i]])));
	                    cd[i] = c[i];
	                }
	                j = N2; // índice de primer flujo de salida
	                t = 0;
	                // abrir flujos de salida
	                for (i = j; i < N; i++) {
	                    flujos[c[i]] = new DataOutputStream(
	                        new BufferedOutputStream(new FileOutputStream(f[c[i]])));
	                }
	                // leer primera clave de cada run
	                for (int n = 0; n < k1; n++) {
	                    flujoEntradaActual = (DataInputStream) flujos[cd[n]];
	                    r[n] = flujoEntradaActual.readInt();
	                }
	                // mezcla de runs
	                while (k1 > 0) {
	                    t++; // un tramo mezclado
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
	                // cerrar flujos de salida
	                for (i = N2; i < N; i++) {
	                    flujoSalidaActual = (DataOutputStream) flujos[c[i]];
	                    flujoSalidaActual.close();
	                }
	                // intercambiar roles de entrada <-> salida
	                for (i = 0; i < N2; i++) {
	                    int a = c[i];
	                    c[i] = c[i + N2];
	                    c[i + N2] = a;
	                }
	            } while (t > 1);

	            System.out.print("Archivo ordenado ...  ");
	            escribir(f[c[0]]);
	        } catch (IOException er) {
	            er.printStackTrace();
	        }
	        return f[c[0]]; // fichero ordenado
	    }


	    private static int minimo(int[] r, boolean[] activo, int n) {
	        int m = TOPE + 1, indice = 0;
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
	            if (activo[i]) 
	            	
	            return false;
	        }
	        return true;
	    }

	    /**
	     * Escribe el contenido de un fichero de enteros en el writer `out`.
	     */
	    static void escribir(File file) {
	        try (DataInputStream flujo = new DataInputStream(
	                new BufferedInputStream(new FileInputStream(file), 64 * 1024))) {
	            int k = 0;
	            while (true) {
	                int val = flujo.readInt();
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


