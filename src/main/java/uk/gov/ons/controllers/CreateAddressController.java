package uk.gov.ons.controllers;

import static uk.gov.ons.util.CreateAddressConstants.BAD_AUX_ADDRESS_FILE_NAME;
import static uk.gov.ons.util.CreateAddressConstants.BAD_UNIT_ADDRESS_FILE_NAME;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.entities.AuxAddress;
import uk.gov.ons.entities.UnitAddress;
import uk.gov.ons.service.AddressService;
import uk.gov.ons.util.ValidatedAddress;

@Slf4j
@Controller
public class CreateAddressController {

	@Autowired
	private AddressService addressService;

	@Value("${aims.gcp.bucket}")
	private String gcsBucket;
	
	@Value("${aims.elasticsearch.cluster.fat-enabled}")
	private boolean fatClusterEnabled;

	@GetMapping(value = "/")
	@ResponseStatus(HttpStatus.OK)
	public String index(Model model) {
		
		model.addAttribute("fatClusterEnabled", fatClusterEnabled);
		
		return "index";
	}

	@PostMapping(value = "/upload-csv-aux-file")
	public String uploadCSVAuxFile(@RequestParam(name = "file") MultipartFile file, Model model) {

		/*
		 * TODO: Reactify the web page to stream the results of the add operation.
		 */

		// validate file
		if (file.isEmpty()) {
			model.addAttribute("message", "Select a CSV file to upload and an Index to load to.");
			model.addAttribute("status", false);
		} else {

			try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {

				CsvToBean<AuxAddress> csvToBean = new CsvToBeanBuilder<AuxAddress>(reader).withType(AuxAddress.class)
						.withIgnoreLeadingWhiteSpace(true).build();

				List<ValidatedAddress<AuxAddress>> validatedAddresses = csvToBean.parse().stream()
						.map(address -> new ValidatedAddress<AuxAddress>(address)).collect(Collectors.toList());

				List<ValidatedAddress<AuxAddress>> invalidAddresses = validatedAddresses.stream()
						.filter(address -> !address.isValid()).collect(Collectors.toList());

				if (invalidAddresses.size() > 0) {
					model.addAttribute("badAddressCSVPath", String.format("Bad addresss file name: %s. In bucket: %s",
							addressService.writeBadAddressesCsv(invalidAddresses, BAD_AUX_ADDRESS_FILE_NAME), gcsBucket));
				}

				List<ValidatedAddress<AuxAddress>> validAddresses = validatedAddresses.stream()
						.filter(address -> address.isValid()).collect(Collectors.toList());

				if (validAddresses.size() > 0) {
					model.addAttribute("addresses", validAddresses);

					// Add the good addresses to Elasticsearch
					addressService.createAuxAddressesFromCsv(validAddresses).doOnNext(output -> {
						log.debug(String.format("Added: %s", output.toString()));

						/*
						 * This is very basic at the moment and just returns to the view the addresses
						 * that were attempted to load into ES. It won't show any that failed. For
						 * example an illegal lat or long will cause the load to fail from that point.
						 * Needs very clean input data.
						 */
					}).subscribe();
				}

				// save address list on model
				model.addAttribute("badAddresses", invalidAddresses);
				model.addAttribute("status", true);
			} catch (Exception ex) {
				model.addAttribute("message",
						String.format("An error occurred while processing the CSV file: %s", ex.getMessage()));
				model.addAttribute("status", false);
			}
		}

		return "file-upload-status";
	}

	@PostMapping(value = "/upload-csv-unit-file")
	public String uploadCSVUnitFile(@RequestParam("file") MultipartFile file, Model model) {

		/*
		 * TODO: Reactify the web page to stream the results of the add operation.
		 */

		// validate file
		if (file.isEmpty()) {
			model.addAttribute("message", "Select a CSV file to upload and an Index to load to.");
			model.addAttribute("status", false);
		} else {

			try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {

				CsvToBean<UnitAddress> csvToBean = new CsvToBeanBuilder<UnitAddress>(reader).withType(UnitAddress.class)
						.withIgnoreLeadingWhiteSpace(true)
						.withSeparator('|').build();

				List<ValidatedAddress<UnitAddress>> validatedAddresses = csvToBean.parse().stream()
						.map(address -> new ValidatedAddress<UnitAddress>(address)).collect(Collectors.toList());

				List<ValidatedAddress<UnitAddress>> invalidAddresses = validatedAddresses.stream()
						.filter(address -> !address.isValid()).collect(Collectors.toList());

				if (invalidAddresses.size() > 0) {
					model.addAttribute("badAddressCSVPath", String.format("Bad addresss file name: %s. In bucket: %s",
							addressService.writeBadAddressesCsv(invalidAddresses, BAD_UNIT_ADDRESS_FILE_NAME), gcsBucket));
				}

				List<ValidatedAddress<UnitAddress>> validAddresses = validatedAddresses.stream()
						.filter(address -> address.isValid()).collect(Collectors.toList());

				if (validAddresses.size() > 0) {
					model.addAttribute("addresses", validAddresses);

					/* Add the good addresses to Elasticsearch
					 * This is very basic at the moment and just returns to the view the addresses
					 * that were attempted to load into ES. It won't show any that failed. For
					 * example an illegal lat or long will cause the load to fail from that point.
					 * Needs very clean input data.
					 */
					if (fatClusterEnabled) {
						addressService.createFatUnitAddressesFromCsv(validAddresses).doOnNext(output -> {
							log.debug(String.format("Added: %s", output.toString()));
						}).subscribe();
					} else {
						addressService.createSkinnyUnitAddressesFromCsv(validAddresses).doOnNext(output -> {
							log.debug(String.format("Added: %s", output.toString()));
						}).subscribe();
					}
				}

				// save address list on model
				model.addAttribute("badAddresses", invalidAddresses);
				model.addAttribute("status", true);
			} catch (Exception ex) {
				model.addAttribute("message",
						String.format("An error occurred while processing the CSV file: %s", ex.getMessage()));
				model.addAttribute("status", false);
			}
		}

		return "unit-address-upload-status";
	}
}
