package ${configuration.daoImpl};

import ${configuration.baseDAO};
import ${configuration.dao}.${name}DAO;
import ${entity.name};
import javax.ejb.Stateless;

/**
 *
 * @author ${author}
 */
@Stateless
public class ${name}DAOImpl extends ${configuration.baseDAOSimpleName}<${name}> implements ${name}DAO {

    @Override
    public Class getEntityClass() {
        return ${name}.class;
    }

}
