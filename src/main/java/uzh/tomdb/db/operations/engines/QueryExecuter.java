
package uzh.tomdb.db.operations.engines;


import java.io.IOException;
import java.util.Collection;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;


import uzh.tomdb.db.operations.Select;
import uzh.tomdb.parser.MalformedSQLQuery;

/**
 * 
 * @author Francesco Luminati
 *
 */
public class QueryExecuter {
//	private final Logger logger = LoggerFactory.getLogger(QueryExecuter.class);  
	private Select select;
	
	public QueryExecuter(Select select) throws MalformedSQLQuery, ClassNotFoundException, IOException {
		this.select = select;
		init();
	}
	
	private void init() throws MalformedSQLQuery, ClassNotFoundException, IOException {
		
		if (select.getTabNames() != null) {
			
			JoinsHandler jHandler = new JoinsHandler(select);
			Collection<Select> selects = jHandler.getSelects();
			switch (select.getScanType()) {
			case "tablescan":
				for (Select select: selects) {
					new TableScan(select, jHandler).start();
				}
				break;
			case "indexscan":
				for (Select select: selects) {
					new IndexScan(select, jHandler).startIndex();
				}
				break;
			}
			
		} else {
			
			ConditionsHandler condHandler = new ConditionsHandler(select);
			switch (select.getScanType()) {
			case "tablescan":
				new TableScan(select, condHandler).start();
				break;
			case "indexscan":
				new IndexScan(select, condHandler).start();
				break;
			}
			
		}
		
	}

}