package ${configuration.businessObject};

import com.xpert.core.crud.AbstractBusinessObject;
import ${configuration.dao}.${name}DAO;
import com.xpert.core.validation.UniqueField;
import com.xpert.core.validation.UniqueFields;
import com.xpert.core.exception.BusinessException;
import java.util.List;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import ${entity.name};

/**
 *
 * @author ${author}
 */
@Stateless
public class ${name}${configuration.businessObjectSuffix} extends AbstractBusinessObject<${name}> {

    @EJB
    private ${name}DAO ${nameLower}DAO;
    
    @Override
    public ${name}DAO getDAO() {
        return ${nameLower}DAO;
    }

    @Override
    public List<UniqueField> getUniqueFields() {
        return UniqueFields.from(${name}.class);
    }

    @Override
    public void validate(${name} ${nameLower}) throws BusinessException {
    }

    @Override
    public boolean isAudit() {
        return true;
    }

}
