package com.ald.fanbei.api.web.validator.intercept;

import java.lang.reflect.Field;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import javax.validation.metadata.ConstraintDescriptor;

import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.common.impl.ApiHandleFactory;
import com.ald.fanbei.api.web.validator.Validator;
import org.apache.commons.beanutils.Converter;

/**
 * 
 * @类描述：数据校验拦截器
 * @author 江荣波 2017年12月29日 下午11:51:19
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("validationInterceptor")
public class ValidationInterceptor implements Interceptor, ApplicationContextAware {

	@Resource
	private ApiHandleFactory apiHandleFactory;

	private ApplicationContext applicationContext;

	private Logger logger = LoggerFactory.getLogger(ValidationInterceptor.class);

	private static javax.validation.Validator clsValidator;
	
	private static ConvertUtilsBean convertUtils;

	@PostConstruct
	public void init() {
		ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
		clsValidator = validatorFactory.getValidator();
		convertUtils = new ConvertUtilsBean();
	}

	@Override
	public void intercept(RequestDataVo reqData, FanbeiContext context, HttpServletRequest request) {
		ApiHandle methodHandel = apiHandleFactory.getApiHandle(reqData.getMethod());
		Class<? extends ApiHandle> clazz = methodHandel.getClass();

		Validator[] validators = getValidatorAnnotation(clazz);
		if (validators != null) {
			Validator validator = validators[0];
			String beanName = validator.value();
			Object validatorBean = applicationContext.getBean(beanName);
			Class<?> validatorBeanClazz = validatorBean.getClass();
			try {
				Object validatorInstanceBean = validatorBeanClazz.newInstance();
				initializeValidatorBean(validatorInstanceBean, reqData);
				Set<ConstraintViolation<Object>> validateResults = clsValidator.validate(validatorInstanceBean);
				for (ConstraintViolation<Object> validateResult : validateResults) {
					Path propertyPath = validateResult.getPropertyPath();
					String message = validateResult.getMessage();
					String paramName = StringUtils.EMPTY;
					if (propertyPath != null) {
						paramName = propertyPath.toString();
					}
					ConstraintDescriptor<?> cd = validateResult.getConstraintDescriptor();
					boolean legal = cd.isReportAsSingleViolation();
					if (!legal) {
						throw new FanbeiException( paramName + message, true);
					}
				}
			} catch (InstantiationException e) {
				logger.error("instantion bean error ,error info =>{}",e.getMessage());
			} catch (IllegalAccessException e) {
				logger.error("illegal access error ,error info =>{}",e.getMessage());
			}
		}
	}

	private void initializeValidatorBean(Object validatorBean, RequestDataVo reqData) {
		Class<? extends Object> clazz = validatorBean.getClass();
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			String fieldName = field.getName();
			// 获取请求参数，初始化ValidationBean
			Object reqParam = reqData.getParams().get(fieldName);
			if (reqParam != null) {
				field.setAccessible(true);
				Class<?> fieldType = field.getType();
				try {
					Converter converter = convertUtils.lookup(fieldType);
					Object fieldVal = converter.convert(fieldType, reqParam);
					field.set(validatorBean, fieldVal);
				} catch (IllegalArgumentException e) {
					logger.error("illegal argument error, error info=>{}", e.getMessage());
				} catch (IllegalAccessException e) {
					logger.error("illegal access error ,error info=>{}", e.getMessage());
				}
			}
		}

	}

	private Validator[] getValidatorAnnotation(Class<? extends ApiHandle> clazz) {
		if (clazz.isAnnotationPresent(Validator.class)) {
			return clazz.getAnnotationsByType(Validator.class);
		}
		return null;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

}
