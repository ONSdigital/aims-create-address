package uk.gov.ons.controllers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

import uk.gov.ons.entities.CSVAddress;
import uk.gov.ons.service.AddressService;

@Controller
public class CreateAddressController {
	
	private Logger logger = LoggerFactory.getLogger(CreateAddressController.class);
	
	@Autowired
	private AddressService addressService;
	
    @GetMapping(value = "/")
    @ResponseStatus(HttpStatus.OK)
    public String index() {
        return "index";
    }

    @PostMapping(value = "/upload-csv-file")
    public String uploadCSVFile(@RequestParam("file") MultipartFile file, Model model) {

        // validate file
        if (file.isEmpty()) {
            model.addAttribute("message", "Select a CSV file to upload.");
            model.addAttribute("status", false);
        } else {

            // parse CSV file
            try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            	
            	CsvToBean<CSVAddress> csvToBean = new CsvToBeanBuilder<CSVAddress>(reader)
					.withType(CSVAddress.class)
					.withIgnoreLeadingWhiteSpace(true)
					.build();
        
           	    List<CSVAddress> addresses = csvToBean.parse();

		        // Add the addresses to Elasticsearch
		        addressService.createAddressesFromCsv(addresses).doOnNext(output -> {
		        	logger.info(String.format("Added: %s", output.toString()));
		        	
		        	/* 
		        	 * This is very basic at the moment and just returns to the view the addresses 
		        	 * that were attempted to load into ES. It won't show any that failed.
		        	 * For example an illegal lat or long will cause the load to fail from that point.
		        	 * Needs very clean input data.
		        	 * TODO: Reactify the web page to stream the results of the add operation. Show what's 
		        	 * been loaded and what failed to load and for what reason.
		        	 */

		        }).subscribe();

                // save address list on model
                model.addAttribute("addresses", addresses);
                model.addAttribute("status", true);

            } catch (Exception ex) {
                model.addAttribute("message", String.format("An error occurred while processing the CSV file: %s", ex.getMessage()) );
                model.addAttribute("status", false);
            }
        }

        return "file-upload-status";
    }
}
