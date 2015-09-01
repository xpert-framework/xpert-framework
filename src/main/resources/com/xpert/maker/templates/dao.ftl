package ${configuration.dao};

import com.xpert.persistence.dao.BaseDAO;
import ${entity.name};
import javax.ejb.Local;

/**
 *
 * @author ${author}
 */
@Local
public interface ${name}DAO extends BaseDAO<${name}> {
    
}
