package ${configuration.dao};

import com.xpert.persistence.dao.BaseDAO;
import ${entity.name};
import jakarta.ejb.Local;

/**
 *
 * @author ${author}
 */
@Local
public interface ${name}DAO extends BaseDAO<${name}> {
    
}
