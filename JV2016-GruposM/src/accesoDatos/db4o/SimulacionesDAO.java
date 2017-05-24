/** 
 * Proyecto: Juego de la vida.
 *  Resuelve todos los aspectos del almacenamiento del DTO Simulacion utilizando un ArrayList.
 *  utilizando almaacenamiento de base de datos db4o.
 *  @since: prototipo2.0
 *  @source: SimulacionesDAO.java 
 *  @version: 2.0 - 2017.05.19 
 *  @author: ajp
 */

package accesoDatos.db4o;


import java.util.List;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Query;

import accesoDatos.DatosException;
import accesoDatos.OperacionesDAO;
import accesoDatos.fichero.MundosDAO;
import accesoDatos.fichero.UsuariosDAO;
import modelo.ModeloException;
import modelo.Mundo;
import modelo.Simulacion;
import modelo.Simulacion.EstadoSimulacion;
import modelo.Usuario;
import util.Fecha;

public class SimulacionesDAO implements OperacionesDAO {

	// Requerido por el Singleton 
	private static SimulacionesDAO instancia;

	// Elemento de almacenamiento.
	private ObjectContainer db;
	

	/**
	 * Constructor por defecto de uso interno.
	 * Sólo se ejecutará una vez.
	 */
	private SimulacionesDAO() {
		db = Conexion.getDB();
		//Si no recibe ningun parámetro carga los datos predeterminados
		if(obtener("III1R")==null){
			cargarPredeterminados();
		}
	}

	/**
	 *  Método estático de acceso a la instancia única.
	 *  Si no existe la crea invocando al constructor interno.
	 *  Utiliza inicialización diferida.
	 *  Sólo se crea una vez; instancia única -patrón singleton-
	 *  @return instancia
	 */
	public static SimulacionesDAO getInstancia() {
		if (instancia == null) {
			instancia = new SimulacionesDAO();
		}
		return instancia;
	}

	/**
	 *  Método para generar de datos predeterminados.
	 */
	private void cargarPredeterminados() {
		// Obtiene usuario (invitado) y mundo predeterminados.
		Usuario usrDemo = UsuariosDAO.getInstancia().obtener("III1R");
		Mundo mundoDemo = MundosDAO.getInstancia().obtener("MundoDemo");
		Simulacion simulacionDemo = null;
		try {
			simulacionDemo = new Simulacion(usrDemo, new Fecha(), mundoDemo, EstadoSimulacion.PREPARADA);
		} catch (ModeloException e) {
			e.printStackTrace();
		}
		db.store(simulacionDemo);
	}

	/**
	 *  Cierra datos.
	 */
	@Override
	public void cerrar() {

	}

	// OPERACIONES DAO
	/**
	 * Búsqueda de Simulacion dado idUsr y fecha.
	 * @param idSimulacion - el idUsr+fecha de la Simulacion a buscar. 
	 * @return - la Simulacion encontrada; null si no existe.
	 */	
	@Override
	public Simulacion obtener(String idSimulacion) {
		Query consulta = db.query();
		consulta.constrain(Simulacion.class);
		consulta.descend("idSimulacion").constrain(idSimulacion).equal();
		ObjectSet <Simulacion> result = consulta.execute();
		if(result.size()>0){
			return result.get(0);
		}
		return null;
	}
	
	/**
	 * Obtiene todas las simulaciones almacenadas
	 * @return-lista con todo.
	 */
	@Override
	public List obtenerTodos() {
		Query consulta = db.query();
		consulta.constrain(Simulacion.class);
		return consulta.execute();
	}

	/**
	 * Búsqueda de simulacion dado un objeto, reenvía al método que utiliza idSimulacion.
	 * @param obj - la Simulacion a buscar.
	 * @return - la Simulacion encontrada; null si no existe.
	 */
	public Simulacion obtener(Object obj)  {
		return this.obtener(((Simulacion) obj).getIdSimulacion());
	}

	/**
	 *  Alta de una nueva Simulacion en orden y sin repeticiones según los idUsr más fecha. 
	 *  Busca previamente la posición que le corresponde por búsqueda binaria.
	 *  @param obj - Simulación a almacenar.
	 * @throws DatosException 
	 *  @ - si ya existe.
	 */	
	public void alta(Object obj) throws DatosException  {
		assert obj !=null;
		Simulacion simulacionNuevo = (Simulacion) obj;
		if(obtener(obj)==null){
			db.store(simulacionNuevo);
		}
		//Excepcion si ya existe la simulacion
		throw new DatosException("(Simulacion) La simulacion: " + simulacionNuevo + " ya existe...");
	}

	/**
	 * Elimina el objeto, dado el id utilizado para el almacenamiento.
	 * @param idSimulacion - identificador de la Simulacion a eliminar.
	 * @return - la Simulacion eliminada. null - si no existe.
	 */
	@Override
	public Simulacion baja(String idSimulacion) throws DatosException {
		assert (idSimulacion != null);
		Simulacion simulacionBorrado = obtener(idSimulacion);
		if(obtener(simulacionBorrado)!=null){
			db.store(simulacionBorrado);
			return simulacionBorrado;
		}
		throw new DatosException("(Simulacion) La simulacion: " + simulacionBorrado + "no existe...");
	}

	
} //class
