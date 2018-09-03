package com.ald.fanbei.api.web.validator.intercept;

import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.Validation;
import javax.validation.metadata.ConstraintDescriptor;

import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;
import org.hibernate.validator.resourceloading.PlatformResourceBundleLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.web.common.Context;
import com.ald.fanbei.api.web.common.JsdH5Handle;
import com.ald.fanbei.api.web.common.JsdH5HandleFactory;
import com.ald.fanbei.api.web.validator.Validator;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;

/**
 * 
 * @类描述：数据校验拦截器
 * @author 江荣波 2017年12月29日 下午11:51:19
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("jsdValidationInterceptor")
public class JsdValidationInterceptor implements Interceptor, ApplicationContextAware {

	@Resource
	private JsdH5HandleFactory jsdH5HandleFactory;

	private ApplicationContext applicationContext;

	private Logger logger = LoggerFactory.getLogger(JsdValidationInterceptor.class);

	private javax.validation.Validator clsValidator;
	
	private ConvertUtilsBean convertUtils;
	
	private ResourceBundle resourceBundle ;

	@PostConstruct
	public void init() {
		clsValidator = Validation.byDefaultProvider()
			       .configure()
			       .messageInterpolator(new ResourceBundleMessageInterpolator(
			    		   new PlatformResourceBundleLocator("validmsg")))
			       .buildValidatorFactory()
			       .getValidator();
		convertUtils = new ConvertUtilsBean();
		resourceBundle = ResourceBundle.getBundle("com.ald.fanbei.api.web.validator.message.check_msg", Locale.CHINA);
	}
	
	@Override
	public void intercept(Context context) {
		JsdH5Handle methodHandle = jsdH5HandleFactory.getHandle(reqData.getMethod());
		Class<? extends JsdH5Handle> clazz = methodHandle.getClass();

		Validator[] validators = getValidatorAnnotation(clazz);
		if (validators != null) {
			Validator validator = validators[0];
			String beanName = validator.value();
			Object validatorBean = applicationContext.getBean(beanName);
			Class<?> validatorBeanClazz = validatorBean.getClass();
			Object validatorInstanceBean = this.resolveValidatorBean(validatorBeanClazz, reqData);
			reqData.setParamObj(validatorInstanceBean);
			
			Set<ConstraintViolation<Object>> validateResults = null;
			synchronized(this) {
				validateResults = clsValidator.validate(validatorInstanceBean);
			}
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
					String transName = StringUtils.EMPTY;
					try{
						transName = resourceBundle.getString(paramName);
					} catch(Exception e) {
						// ignore error
					}
					if(StringUtils.isNotEmpty(transName)) {
						try {
							paramName = new String(transName.getBytes("ISO-8859-1"), "UTF-8");
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
					}
					logger.error(paramName + message);
					throw new FanbeiException( paramName + message, true);
				}
			}
		}
	}
	
	private Object resolveValidatorBean(Class<?> validatorBeanClazz) {
		return JSON.parseObject(JSON.toJSONString(reqData.getParams()), validatorBeanClazz);
	}
	private Validator[] getValidatorAnnotation(Class<?> clazz) {
		if (clazz.isAnnotationPresent(Validator.class)) {
			Annotation[] annotations = clazz.getDeclaredAnnotations();
			List<Validator> vas = Lists.newArrayList();
			for(Annotation annotation: annotations) {
				if(annotation instanceof Validator) {
					vas.add((Validator)annotation);
				}
			}
			Validator[] validators = {} ;
			validators = vas.toArray(validators);
			return validators;
		}
		return null;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

}
