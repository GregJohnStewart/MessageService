package mil.army.dcgs.messageService.interfaces.ui;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import lombok.extern.slf4j.Slf4j;
import mil.army.dcgs.messageService.interfaces.RestInterface;

@Slf4j
public abstract class UiEndpoint extends RestInterface {
	
	protected abstract Template getPageTemplate();
	
	protected TemplateInstance getDefaultPageSetup(Template template){
		return template.instance();
	}
	
	protected TemplateInstance getDefaultPageSetup(){
		return this.getDefaultPageSetup(this.getPageTemplate());
	}
}
