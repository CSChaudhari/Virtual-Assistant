package com.example.VirtualAssistant;

import com.plivo.api.exceptions.PlivoValidationException;
import com.plivo.api.exceptions.PlivoXmlException;
import com.plivo.api.xml.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@SpringBootApplication
@RestController
public class VirtualAssistantApplication {
    public static void main(final String[] args) {
        SpringApplication.run(VirtualAssistantApplication.class, args);
    }

    // Welcome message - firstbranch
    String welcomeMessage = "Welcome to the demo app, Say Sales to talk to our Sales representative. Say Support to talk to our Support representative";
    // This is the message that Plivo reads when the caller does nothing at all
    String noInput = "Sorry, I didn't catch that. Please hangup and try again later.";
    // This is the message that Plivo reads when the caller inputs a wrong digit.
    String wrongInput = "Sorry, it's a wrong input.";

    @GetMapping(value = "/virtual_assistant/", produces = {
            "application/xml"
    })

    public Response getInput(HttpServletRequest request) throws PlivoXmlException, PlivoValidationException {
        String hostName = request.getRequestURL().toString();
        final Response response = new Response().children(
                        new GetInput().action(hostName + "firstbranch/").method("POST")
                                .interimSpeechResultsCallback(hostName + "firstbranch/")
                                .interimSpeechResultsCallbackMethod("POST").inputType("speech").redirect(true)
                                .children(new Speak(welcomeMessage)))
                .children(new Speak(noInput));
        System.out.println(response.toXmlString());
        return response;
    }

    @RequestMapping(value = "/virtual_assistant/firstbranch/", produces = {
            "application/xml"
    }, method = RequestMethod.POST)
    public Response firstbranch(HttpServletRequest request, @RequestParam("Speech") final String speech,
                                @RequestParam("From") final String fromNumber) throws PlivoXmlException, PlivoValidationException {
        System.out.println("Speech Input is:" + speech);
        String hostName = request.getRequestURL().toString();
        final Response response = new Response();
        if (speech.equals("sales")) {
            response.children(
                    new Dial().callerId(fromNumber).action(hostName + "action/")
                            .method("POST").redirect(false).children(new Number("<number_1>")));
        } else if (speech.equals("support")) {
            response.children(
                    new Dial().callerId(fromNumber).action(hostName + "action/")
                            .method("POST").redirect(false).children(new Number("<number_2>")));
        } else {
            response.children(new Speak(wrongInput));
        }
        System.out.println(response.toXmlString());
        return response;
    }
}
