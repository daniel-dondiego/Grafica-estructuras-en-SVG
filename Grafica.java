package mx.unam.ciencias.edd;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Clase para gráficas. Una gráfica es un conjunto de vértices y
 * aristas, tales que las aristas son un subconjunto del producto
 * cruz de los vértices.
 */
public class Grafica<T> implements Iterable<T> {

    /* Clase privada para iteradores de gráficas. */
    private class Iterador<T> implements Iterator<T> {

        /* Iterador auxiliar. */
        private Iterator<Grafica<T>.Vertice<T>> iterador;

        /* Construye un nuevo iterador, auxiliándose del diccionario
         * de vértices. */
        public Iterador(Grafica<T> grafica) {
            iterador = grafica.vertices.iterator();
        }

        /* Nos dice si hay un siguiente elemento. */
        public boolean hasNext() {
            return iterador.hasNext();
        }

        /* Regresa el siguiente elemento. */
        public T next() {
            return iterador.next().elemento;
        }

        /* No lo implementamos: siempre lanza una excepción. */
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    /* Aristas para gráficas; para poder guardar el peso de las
     * aristas. */
    private class Arista<T> {

        /* El vecino del vértice. */
        public Grafica<T>.Vertice<T> vecino;
        /* El peso de arista conectando al vértice con el vecino. */
        public double peso;

        public Arista(Grafica<T>.Vertice<T> vecino, double peso) {
            this.vecino = vecino;
            this.peso = peso;
        }
    }

    /* Vertices para gráficas; implementan la interfaz
     * ComparableIndexable y VerticeGrafica */
    private class Vertice<T> implements ComparableIndexable<Vertice<T>>,
        VerticeGrafica<T> {

        /* Iterador para las vecinos del vértice. */
        private class IteradorVecinos implements Iterator<VerticeGrafica<T>> {

            /* Iterador auxiliar. */
            private Iterator<Grafica<T>.Arista<T>> iterador;

            /* Construye un nuevo iterador, auxiliándose del
             * diccionario de vecinos. */
            public IteradorVecinos(Iterator<Grafica<T>.Arista<T>> iterador) {
                this.iterador = iterador;
            }

            /* Nos dice si hay un siguiente vecino. */
            public boolean hasNext() {
                return iterador.hasNext();
            }

            /* Regresa el siguiente vecino. La audición es
             * inevitable. */
            public VerticeGrafica<T> next() {
                Grafica<T>.Arista<T> arista = iterador.next();
                return (VerticeGrafica<T>)arista.vecino;
            }

            /* No lo implementamos: siempre lanza una excepción. */
            public void remove() {
                throw new UnsupportedOperationException();
            }
        }

        /* El elemento del vértice. */
        public T elemento;
        /* El color del vértice. */
        public Color color;
        /* La distancia del vértice. */
        public double distancia;
        /* El índice del vértice. */
        public int indice;
        /* El diccionario de aristas que conectan al vértice con sus
         * vecinos. */
        public Diccionario<T, Grafica<T>.Arista<T>> aristas;

        /* Crea un nuevo vértice a partir de un elemento. */
        public Vertice(T elemento) {
            this.elemento = elemento;
            color = Color.NINGUNO;
            aristas = new Diccionario<T,Grafica<T>.Arista<T>>();
        }

        /* Regresa el elemento del vértice. */
        public T getElemento() {
            return elemento;
        }

        /* Regresa el grado del vértice. */
        public int getGrado() {
            return aristas.getTotal();
        }

        /* Regresa el color del vértice. */
        public Color getColor() {
            return color;
        }

        /* Define el color del vértice. */
        public void setColor(Color color) {
            this.color = color;
        }

        /* Regresa un iterador para los vecinos. */
        public Iterator<VerticeGrafica<T>> iterator() {
            return new IteradorVecinos(aristas.iterator());
        }

        /* Define el índice del vértice. */
        public void setIndice(int indice) {
            this.indice = indice;
        }

        /* Regresa el índice del vértice. */
        public int getIndice() {
            return indice;
        }

        /* Compara dos vértices por distancia. */
        public int compareTo(Vertice<T> vertice) {
            return 0;
        }
    }

    /* Vértices. */
    private Diccionario<T, Vertice<T>> vertices;
    /* Número de aristas. */
    private int aristas;

    /**
     * Constructor único.
     */
    public Grafica() {
            vertices = new Diccionario<T, Vertice<T>>();
            aristas = 0;
    }

    /**
     * Regresa el número de vértices.
     * @return el número de vértices.
     */
    public int getVertices() {
        return vertices.getTotal();
    }

    /**
     * Regresa el número de aristas.
     * @return el número de aristas.
     */
    public int getAristas() {
        return aristas;
    }

    /**
     * Agrega un nuevo elemento a la gráfica.
     * @param elemento el elemento a agregar.
     * @throws IllegalArgumentException si el elemento ya había sido
     *         agregado a la gráfica.
     */
    public void agrega(T elemento) {
        if(!contiene(elemento))
            vertices.agrega(elemento,new Vertice<T>(elemento));
        else 
            throw new IllegalArgumentException();
    }

    /**
     * Conecta dos elementos de la gráfica. Los elementos deben
     * estar en la gráfica. El peso de la arista que conecte a los
     * elementos será 1.
     * @param a el primer elemento a conectar.
     * @param b el segundo elemento a conectar.
     * @throws NoSuchElementException si a o b no son elementos de
     *         la gráfica.
     * @throws IllegalArgumentException si a o b ya están
     *         conectados, o si a es igual a b.
     */
    public void conecta(T a, T b) {
        if(!contiene(a) || !contiene(b))
            throw new NoSuchElementException();
        if(sonVecinos(a,b)||a == b)
            throw new IllegalArgumentException();
        Vertice<T> v1 = buscaVertice(a),
        v2 = buscaVertice(b);
        v1.aristas.agrega(b,new Arista<T>(v2,1));
        v2.aristas.agrega(a,new Arista<T>(v1,1));
        aristas++;
    }

    /**
     * Conecta dos elementos de la gráfica. Los elementos deben
     * estar en la gráfica.
     * @param a el primer elemento a conectar.
     * @param b el segundo elemento a conectar.
     * @param peso el peso de la nueva arista.
     * @throws NoSuchElementException si a o b no son elementos de
     *         la gráfica.
     * @throws IllegalArgumentException si a o b ya están
     *         conectados, o si a es igual a b.
     */
    public void conecta(T a, T b, double peso) {
        if(!contiene(a) || !contiene(b))
            throw new NoSuchElementException();
        if(sonVecinos(a,b)||a == b)
            throw new IllegalArgumentException();
        Vertice<T> v1 = buscaVertice(a),
        v2 = buscaVertice(b);
        v1.aristas.agrega(b,new Arista<T>(v2,peso));
        v2.aristas.agrega(a,new Arista<T>(v1,peso));
        aristas++;
    }

    /**
     * Desconecta dos elementos de la gráfica. Los elementos deben
     * estar en la gráfica y estar conectados entre ellos.
     * @param a el primer elemento a desconectar.
     * @param b el segundo elemento a desconectar.
     * @throws NoSuchElementException si a o b no son elementos de
     *         la gráfica.
     * @throws IllegalArgumentException si a o b no están
     *         conectados.
     */
    public void desconecta(T a, T b) {
        Vertice<T> v1 = buscaVertice(a),
        v2 = buscaVertice(b);
        if(v1 == null || v2 == null)
            throw new NoSuchElementException();
        if(!sonVecinos(a,b))
            throw new IllegalArgumentException();
        for(Arista<T> arista : v2.aristas){
            if(arista.vecino == v1)
                v2.aristas.elimina(a);
        }
        for(Arista<T> arista : v1.aristas){
            if(arista.vecino == v2)
                v1.aristas.elimina(b);
        }
        aristas--;
    }

    /**
     * Nos dice si el elemento está contenido en la gráfica.
     * @return <tt>true</tt> si el elemento está contenido en la
     *         gráfica, <tt>false</tt> en otro caso.
     */
    public boolean contiene(T elemento) {
        return buscaVertice(elemento) != null;
    }

    /**
     * Elimina un elemento de la gráfica. El elemento tiene que
     * estar contenido en la gráfica.
     * @param elemento el elemento a eliminar.
     * @throws NoSuchElementException si el elemento no está
     *         contenido en la gráfica.
     */
    public void elimina(T elemento) {
        Vertice<T> ve = buscaVertice(elemento);
         if(ve != null){
            for(Arista<T> arista : ve.aristas)
                desconecta(arista.vecino.elemento,elemento);
            vertices.elimina(elemento);
        }else
            throw new NoSuchElementException();
    }

    /**
     * Nos dice si dos elementos de la gráfica están conectados. Los
     * elementos deben estar en la gráfica.
     * @param a el primer elemento.
     * @param b el segundo elemento.
     * @return <tt>true</tt> si a y b son vecinos, <tt>false</tt> en
     *         otro caso.
     * @throws NoSuchElementException si a o b no son elementos de
     *         la gráfica.
     */
    public boolean sonVecinos(T a, T b) {
        if(!contiene(a) || !contiene(b))
            throw new NoSuchElementException();
        Vertice<T> vA = buscaVertice(a),
        vB = buscaVertice(b);
        for(Arista<T> arista : vA.aristas){
            if(arista.vecino == vB)
                return true;
        }
        return false;
    }

    /**
     * Regresa el peso de la arista que comparten los vértices que
     * contienen a los elementos recibidos.
     * @param a el primer elemento.
     * @param b el segundo elemento.
     * @return el peso de la arista que comparten los vértices que
     *         contienen a los elementos recibidos, o -1 si los
     *         elementos no están conectados.
     * @throws NoSuchElementException si a o b no son elementos de
     *         la gráfica.
     */
    public double getPeso(T a, T b) {
        Vertice<T> vA = buscaVertice(a),
                   vB = buscaVertice(b);
        if(vA == null || vB == null)
            throw new NoSuchElementException();
        double peso = -1;
        for(Arista<T> arista : vA.aristas){
            if(arista.vecino == vB)
                peso = arista.peso;
        }
        return peso;
    }

    /**
     * Regresa el vértice correspondiente el elemento recibido.
     * @throws NoSuchElementException si elemento no es elemento de
     *         la gráfica.
     * @return el vértice correspondiente el elemento recibido.
     */
    public VerticeGrafica<T> vertice(T elemento) {
        Vertice<T> v = buscaVertice(elemento);
        if(v == null)
            throw new NoSuchElementException();
        return v;
    }

    /**
     * Realiza la acción recibida en cada uno de los vértices de la
     * gráfica, en el orden en que fueron agregados.
     * @param accion la acción a realizar.
     */
    public void paraCadaVertice(AccionVerticeGrafica<T> accion) {
        for(Vertice<T> v : vertices)
            accion.actua(v);
    }

    /**
     * Realiza la acción recibida en todos los vértices de la
     * gráfica, en el orden determinado por BFS, comenzando por el
     * vértice correspondiente al elemento recibido. Al terminar el
     * método, todos los vértices tendrán color {@link
     * Color#NINGUNO}.
     * @param elemento el elemento sobre cuyo vértice queremos
     *        comenzar el recorrido.
     * @param accion la acción a realizar.
     * @throws NoSuchElementException si el elemento no está en la
     *         gráfica.
     */
    public void bfs(T elemento, AccionVerticeGrafica<T> accion) {
        Vertice<T> v = buscaVertice(elemento);
        if(v == null)
            throw new NoSuchElementException();
        Cola<Vertice<T>> cola = new Cola<Vertice<T>>();
        recorridos(v,accion,cola);
    }

    /**
     * Realiza la acción recibida en todos los vértices de la
     * gráfica, en el orden determinado por DFS, comenzando por el
     * vértice correspondiente al elemento recibido. Al terminar el
     * método, todos los vértices tendrán color {@link
     * Color#NINGUNO}.
     * @param elemento el elemento sobre cuyo vértice queremos
     *        comenzar el recorrido.
     * @param accion la acción a realizar.
     * @throws NoSuchElementException si el elemento no está en la
     *         gráfica.
     */
    public void dfs(T elemento, AccionVerticeGrafica<T> accion) {
        Vertice<T> v = buscaVertice(elemento);
        if(v == null)
            throw new NoSuchElementException();
        Pila<Vertice<T>> pila = new Pila<Vertice<T>>();
        recorridos(v,accion,pila);
    }

    /**
     * Hace BFS o DFS dependiendo el caso
     * @param v el vertice del cual queremos comenzar el recorrido.
     * @param accion la acción a realizar.
     * @param pc que recibe o una pila si es DFS o una cola si es BFS.
     */
    private void recorridos(Vertice<T> v,AccionVerticeGrafica<T> accion,MeteSaca<Vertice<T>> pc){
        for(Vertice<T> v2 : vertices)
            v2.color = Color.NEGRO;
        pc.mete(v);
        v.color = Color.ROJO;
         while(!pc.esVacia()){
            v = pc.saca();
            accion.actua(v);
            /* Metemos a los vecinos del vértice actual y los marcamos */
            for(Arista<T> a : v.aristas){
                if(a.vecino.color == Color.NEGRO){
                    pc.mete(a.vecino);
                    a.vecino.color = Color.ROJO;
                }
            }
        }
        /* Regresamos el color a los vértices */
        for(Vertice<T> v1 : vertices)
            v1.color = Color.NINGUNO;
    }

    /* Método para buscar un vértice */
    private Vertice<T> buscaVertice(T elemento){
        try{
            return vertices.get(elemento);
        }catch(NoSuchElementException nsee){ 
            return null; 
        }
    }

    /* Método para buscar una arista por su vecino */
    private Arista<T> buscaArista(Vertice<T> v , T elemento){
        try{
            return v.aristas.get(elemento);
        }catch(NoSuchElementException nsee){
                return null;
        }
    }

    /**
     * Regresa un iterador para iterar la gráfica. La gráfica se
     * itera en el orden en que fueron agregados sus elementos.
     * @return un iterador para iterar la gráfica.
     */
    @Override public Iterator<T> iterator() {
        return new Iterador<T>(this);
    }

    /**
     * Calcula una trayectoria de distancia mínima entre dos
     * vértices.
     * @param origen el vértice de origen.
     * @param destino el vértice de destino.
     * @return Una lista con vértices de la gráfica, tal que forman
     *         una trayectoria de distancia mínima entre los
     *         vértices <tt>a</tt> y <tt>b</tt>. Si los elementos se
     *         encuentran en componentes conexos distintos, el
     *         algoritmo regresa una lista vacía.
     * @throws NoSuchElementException si alguno de los dos elementos
     *         no está en la gráfica.
     */
    public Lista<VerticeGrafica<T>> trayectoriaMinima(T origen, T destino) {
        Vertice<T> ori = buscaVertice(origen); 
    Vertice<T> dest = buscaVertice(destino); 
    Lista<VerticeGrafica<T>> l=new Lista<VerticeGrafica<T>>(); 
    if(ori == null || dest == null) throw new NoSuchElementException();
    Cola<Vertice<T>> cola = new Cola<Vertice<T>>(); cola.mete(ori); ori.color=Color.ROJO; 
    if(!auxiliar(cola, dest)) return l; l.agregaFinal(dest); auxiliar1(dest, l); return l; 
    }

private boolean auxiliar(Cola<Vertice<T>> cola, Vertice<T> vertice){ 
    if(cola.esVacia()) 
        return false; 
    Vertice<T> v = cola.saca(); 
    if(v == vertice) return true; 
    for(Arista<T> a : v.aristas){ 
        if(a.vecino.color != Color.ROJO){ 
            a.vecino.color=Color.ROJO; 
            a.vecino.distancia = v.distancia + 1; cola.mete(a.vecino); 
        } 
    } return auxiliar(cola,vertice); 
} 

private void auxiliar1(Vertice<T> v, Lista<VerticeGrafica<T>> lista){ 
    if(v.distancia == 0) return; 
    for(Arista<T> arista : v.aristas){ 
        if(arista.vecino.distancia + 1 == v.distancia){ 
            lista.agregaInicio(arista.vecino); 
            auxiliar1(arista.vecino,lista); 
            break; 
        } 
    } 
}

    /**
     * Calcula la ruta de peso mínimo entre el elemento de origen y
     * el elemento de destino.
     * @param origen el vértice origen.
     * @param destino el vértice destino.
     * @return una trayectoria de peso mínimo entre el vértice
     *         <tt>origen</tt> y el vértice <tt>destino</tt>. Si los
     *         vértices están en componentes conexas distintas,
     *         regresa una lista vacía.
     * @throws NoSuchElementException si alguno de los dos elementos
     *         no está en la gráfica.
     */
    public Lista<VerticeGrafica<T>> dijkstra(T ori, T dest) {
         Vertice<T> origen  = buscaVertice(ori),
                    destino = buscaVertice(dest);
        if(origen == null || destino == null)
            throw new NoSuchElementException();
        /* Todos los vértices tienen distancia infinta */
        for(Vertice<T> v : vertices)
            v.distancia = Double.POSITIVE_INFINITY;
        /* La distancia del origen es 0 */
        origen.distancia = 0;
        /* Se crea un montículo con los vértices de la gráfica y una lista */
        Lista<VerticeGrafica<T>> dijkstrAlgorythm = new Lista<VerticeGrafica<T>>();
        Lista<Vertice<T>> l = new Lista<Vertice<T>>();
        for(Vertice<T> v : vertices)
            l.agregaInicio(v);
        MonticuloMinimo<Vertice<T>> monticulo = new MonticuloMinimo<Vertice<T>>(l);
        while(!monticulo.esVacio()){
            Vertice<T> v = monticulo.elimina();
            for(Arista<T> arista : v.aristas){
                if((v.distancia + arista.peso) < arista.vecino.distancia){
                    arista.vecino.distancia = v.distancia + arista.peso;
                    monticulo.reordena(arista.vecino);
                }
            }
        }
        /* Se verifica si alguno de los vértices están en componentes conexas distintas */
        if(origen.distancia == Double.POSITIVE_INFINITY || destino.distancia == Double.POSITIVE_INFINITY)
            return dijkstrAlgorythm;
        return dijkstra(destino,dijkstrAlgorythm);
    }


     public String generaScalableVectorGraphics() {
        String cad = "<?xml version='1.0' encoding='UTF-8'?> \n<svg width='"+ ((100 * vertices.getTotal())+100)+"' height='"+((100 * vertices.getTotal())+100)+"'> \n <g>";
       if(vertices.getTotal() == 0)
        	return "<?xml version='1.0' encoding='UTF-8'?> \n<svg width='100' height='100'> \n <g>\n</g>\n</svg>";
        cad += "\n<rect width='"+((100 * vertices.getTotal())+100)+"' height='"+((100 * vertices.getTotal())+100)+"' x = '0' y = '0' style='fill:rgb(10000,10000,10000);stroke-width:1;stroke:rgb(0,0,0)'/>";
        int i = 0;
        for (Vertice<T> v : vertices) {
        	v.setIndice(i++);
        }
        for(Vertice<T> v: vertices){
        	for (Arista<T> a: v.aristas){
        		double x1,y1,x2,y2;
        		x1 = obtenerX(v);
        		y1 = obtenerY(v);
        		x2 = obtenerX(a.vecino);
        		y2 = obtenerY(a.vecino);

			cad += "<line stroke='black' stroke-width='1' x1='"+x1+"' y1='"+y1+"' x2='"+x2+"' y2='"+y2+"' />\n"; 
        	cad += "<text fill='red' font-family='sans-serif' font-size='20' x='"+((x1+x2)/2+20)+"' y='"+((y1+y2)/2-10)+"' text-anchor='middle'>"+a.peso+"</text>\n"; 

        	}
        }
        for (Vertice<T> v: vertices) {
        	cad += "<circle cx='"+obtenerX(v)+"' cy='"+obtenerY(v)+"' r='20' stroke='black' stroke-width='3' fill='white'></circle>\n"; 
        cad += "<text fill='black' font-family='sans-serif' font-size='20' x='"+obtenerX(v)+"' y='"+(obtenerY(v)+8)+"' text-anchor='middle'>"+v.elemento+"</text>\n"; 
        	
        }
        return cad += "\n</g>\n</svg>";
    }

    private double obtenerX(Vertice<T> v){
    	double r = 50 * vertices.getTotal();
    	double z = 50;
    	return r-(r*Math.sin(((2*Math.PI)/vertices.getTotal())*v.getIndice())) + z;

    }

    private double obtenerY(Vertice<T> v){
    	double r = 50 * vertices.getTotal();
    	double z = 50;
    	return r-(r*Math.cos(((2*Math.PI)/vertices.getTotal())*v.getIndice())) + z;

    }

    public String generaScalableVectorGraphics(Lista<VerticeGrafica<T>> l){
    	String s = generaScalableVectorGraphics();
    	String nS = s.replace("\n</g>\n</svg>","");
    	int i = 0;
    	int j = 1;
    	if(l.getLongitud() > 1){
    		while(j<l.getLongitud()){
    				Vertice<T> v = buscaVertice(l.get(i++).getElemento());
    				Vertice<T> v1 = buscaVertice(l.get(j++).getElemento());
    				nS += "<line stroke='red' stroke-width='4' x1='"+obtenerX(v)+"' y1='"+obtenerY(v)+"' x2='"+obtenerX(v1)+"' y2='"+obtenerY(v1)+"' />\n"; 
  				nS += "<circle cx='"+obtenerX(v)+"' cy='"+obtenerY(v)+"' r='20' stroke='black' stroke-width='3' fill='white'></circle>\n"; 
        nS += "<text fill='black' font-family='sans-serif' font-size='20' x='"+obtenerX(v)+"' y='"+(obtenerY(v)+8)+"' text-anchor='middle'>"+v.elemento+"</text>\n"; 
  					if(j == l.getLongitud()){
  						v = buscaVertice(l.get(i).getElemento());
  					nS += "<circle cx='"+obtenerX(v)+"' cy='"+obtenerY(v)+"' r='20' stroke='black' stroke-width='3' fill='white'></circle>\n"; 
        		nS += "<text fill='black' font-family='sans-serif' font-size='20' x='"+obtenerX(v)+"' y='"+(obtenerY(v)+8)+"' text-anchor='middle'>"+v.elemento+"</text>\n"; 
    			}
    		}

    		return nS += "\n</g>\n</svg>";
    	}
    	else
      return nS += "\n</g>\n</svg>";
    }


    /* Método auxiliar que hace el regreso desde el vértice destino para dijkstra */
    private Lista<VerticeGrafica<T>> dijkstra(Vertice<T> vertice,Lista<VerticeGrafica<T>> lista){
        lista.agregaInicio(vertice);
        if(vertice.distancia == 0)
            return lista;
        Vertice<T> v = null;
        for(Arista<T> arista : vertice.aristas){
            if(vertice.distancia - arista.peso == arista.vecino.distancia)
                 v = arista.vecino;
        } 
        return dijkstra(v,lista);
    }

}
