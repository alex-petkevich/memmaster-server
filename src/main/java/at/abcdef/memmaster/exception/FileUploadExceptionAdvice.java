package at.abcdef.memmaster.exception;

import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import at.abcdef.memmaster.controllers.dto.response.MessageResponse;
import at.abcdef.memmaster.service.TranslateService;

@ControllerAdvice
public class FileUploadExceptionAdvice
{
	final
	TranslateService translate;

	public FileUploadExceptionAdvice(TranslateService translate) {
		this.translate = translate;
	}

	@ExceptionHandler(MaxUploadSizeExceededException.class)
	public ResponseEntity<MessageResponse> handleMaxSizeException(MaxUploadSizeExceededException exc)
	{
		return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new MessageResponse(translate.get("file-upload.file-too-large")));
	}
}
