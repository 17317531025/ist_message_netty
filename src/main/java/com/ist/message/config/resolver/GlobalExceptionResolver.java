package com.ist.message.config.resolver;

import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import com.ist.message.common.Exception.MessageException;
import com.ist.message.common.ResultConstant;
import com.ist.message.common.util.MapperUtils;
import com.ist.message.controller.model.BaseApiResp;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class GlobalExceptionResolver implements HandlerExceptionResolver {
    @Override
    public ModelAndView resolveException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception ex) {
        ModelAndView mv = new ModelAndView(new MappingJackson2JsonView());
        BaseApiResp result = new BaseApiResp();
        if(ex instanceof MessageException){
            MessageException expt = (MessageException) ex;

            /**
             ResultVo result = ResultVo.builder()
             .code(expt.getCode())
             .msg(expt.getMessage())
             .build();
             */
            result.setCode(expt.getCode());
            result.setMsg(expt.getMessage());
        }else if(ex instanceof BindException || ex instanceof MethodArgumentNotValidException){
            FieldError fieldError = ex instanceof BindException ?
                    ((BindException) ex).getBindingResult().getFieldError():
                    ((MethodArgumentNotValidException) ex).getBindingResult().getFieldError();

            /**
             ResultVo result = ResultVo.builder()
             .code(ResultConstant.PARAM_ERROR_CODE)
             .msg(fieldError.getDefaultMessage())
             .build();
             */
            result.setCode(ResultConstant.PARAM_ERROR_CODE);
            result.setMsg(fieldError.getDefaultMessage());
        } else if (ex instanceof ConstraintViolationException){
            Set<ConstraintViolation<?>> constraintViolations = ((ConstraintViolationException) ex).getConstraintViolations();
            if(CollectionUtils.isNotEmpty(constraintViolations)){
                StringBuilder msgBuilder = new StringBuilder();
                for(ConstraintViolation constraintViolation :constraintViolations){
                    msgBuilder.append(constraintViolation.getMessage()).append(",");
                }
                String errorMessage = msgBuilder.toString();
                if(errorMessage.length()>1){
                    errorMessage = errorMessage.substring(0,errorMessage.length()-1);
                }

                /**
                 ResultVo result = ResultVo.builder()
                 .code(ResultConstant.PARAM_ERROR_CODE)
                 .msg(errorMessage)
                 .build();
                 */

                result.setCode(ResultConstant.PARAM_ERROR_CODE);
                result.setMsg(errorMessage);
            }
        } else if(ex instanceof MissingServletRequestParameterException){
            result.setCode(ResultConstant.PARAM_ERROR_CODE);
            result.setMsg(ex.getMessage());
            log.info("exception in params:{}",ex.getMessage());
            ex.printStackTrace();
        }else {
            /**
             ResultVo result = ResultVo.builder()
             .code(ResultVo.ERROR)
             .msg("请求异常")
             .build();
             */
            result.setCode(ResultConstant.APP_ERROR_CODE);
            result.setMsg("系统异常");
            log.info("exception in service:{}",ex.getMessage());

            ex.printStackTrace();

        }
        Map<String,Object> resultMap = MapperUtils.mapAsMap(result);
        log.info("GlobalExceptionResolver.resp:{}", resultMap);
        mv.addAllObjects(resultMap);

        return mv;
    }
}
