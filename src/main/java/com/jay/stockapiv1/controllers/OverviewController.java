package com.jay.stockapiv1.controllers;

import com.jay.stockapiv1.models.Overview;
import com.jay.stockapiv1.repositories.OverviewRepository;
import com.jay.stockapiv1.utils.ApiErrorHandling;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/overview")
public class OverviewController {

    @Autowired
    private Environment env;

    @Autowired
    private OverviewRepository overviewRepository;

    private final String BASE_URL = "https://www.alphavantage.co/query?function=OVERVIEW";

    // http://localhost:8080/api/overview/test
    @GetMapping("/test")
    private ResponseEntity<?> testOverview(RestTemplate restTemplate) {
        try {

            String url = BASE_URL + "&symbol=IBM&apikey=demo";

            Overview responseBody = restTemplate.getForObject(url, Overview.class);

            return ResponseEntity.ok(responseBody);

        } catch(IllegalArgumentException e) {

            return ApiErrorHandling.customApiError("Error In test overview: Check URL used for AV Request", HttpStatus.INTERNAL_SERVER_ERROR);

        } catch (Exception e) {

            return ApiErrorHandling.genericApiError(e);

        }
    }

    //TEST UPLOAD TO DB
    @PostMapping("/test")
    private ResponseEntity<?> testUploadOverview(RestTemplate restTemplate) {
        try {

            String url = BASE_URL + "&symbol=IBM&apikey=demo";

            Overview responseBody = restTemplate.getForObject(url, Overview.class);

            if (responseBody == null) {

                return ApiErrorHandling.customApiError("Did not receive response from AV", HttpStatus.INTERNAL_SERVER_ERROR);

            }

            else if (responseBody.getSymbol() == null) {

                return ApiErrorHandling.customApiError("No Data Retrieved From AV", HttpStatus.NOT_FOUND);

            }

            Overview savedOverview = overviewRepository.save(responseBody);

            return ResponseEntity.ok(savedOverview);

        } catch (DataIntegrityViolationException e) {

            return ApiErrorHandling.customApiError("Can not upload duplicate Stock data", HttpStatus.BAD_REQUEST);

        }
        catch(IllegalArgumentException e) {

            return ApiErrorHandling.customApiError("Error In test overview: Check URL used for AV Request", HttpStatus.INTERNAL_SERVER_ERROR);

        } catch (Exception e) {

            return ApiErrorHandling.genericApiError(e);

        }
    }

    // http://localhost:8080/api/overview/{symbol}
    @GetMapping("/{symbol}")
    public ResponseEntity<?> dynamicOverview(RestTemplate restTemplate, @PathVariable String symbol) {
        try {

            String apiKey = env.getProperty("AV_API_KEY");

            String url = BASE_URL + "&symbol=" + symbol + "&apikey=" + apiKey;

            System.out.println(url);

            Overview responseBody = restTemplate.getForObject(url, Overview.class);

            if (responseBody == null) {

                return ApiErrorHandling.customApiError("Did not receive response from AV", HttpStatus.INTERNAL_SERVER_ERROR);

            }

            else if (responseBody.getSymbol() == null) {

                return ApiErrorHandling.customApiError("Invalid Stock Symbol: " + symbol, HttpStatus.NOT_FOUND);

            }

            return ResponseEntity.ok(responseBody);

        } catch (Exception e) {

            return ApiErrorHandling.genericApiError(e);

        }
    }

    @PostMapping("/{symbol}")
    public ResponseEntity<?> uploadOverview(RestTemplate restTemplate, @PathVariable String symbol) {
        try {

            String apiKey = env.getProperty("AV_API_KEY");

            String url = BASE_URL + "&symbol=" + symbol + "&apikey=" + apiKey;

            Overview responseBody = restTemplate.getForObject(url, Overview.class);

            if (responseBody == null) {

                return ApiErrorHandling.customApiError("Did not receive response from AV", HttpStatus.INTERNAL_SERVER_ERROR);

            }

            else if (responseBody.getSymbol() == null) {

                return ApiErrorHandling.customApiError("Invalid Stock Symbol: " + symbol, HttpStatus.NOT_FOUND);

            }

            Overview savedOverview = overviewRepository.save(responseBody);

            return ResponseEntity.ok(savedOverview);

        } catch (DataIntegrityViolationException e) {

            return ApiErrorHandling.customApiError("Can not upload duplicate Stock data", HttpStatus.BAD_REQUEST);

        }
        catch (Exception e) {

            return ApiErrorHandling.genericApiError(e);

        }
    }

    @GetMapping("/all")
    private ResponseEntity<?> getAllStockData () {
        try {

            Iterable<Overview> allStockData= overviewRepository.findAll();

            return new ResponseEntity<>(allStockData, HttpStatus.OK);

        } catch (Exception e) {

            return ApiErrorHandling.genericApiError(e);

        }
    }

    @GetMapping("/stockid/{id}")
    private ResponseEntity<?> getStockId (@PathVariable String id) {
        try {

            Optional<Overview> stockId = overviewRepository.findById(Long.parseLong(id));

           if (stockId.isEmpty()) {

                return ApiErrorHandling.customApiError("did not match any overview", HttpStatus.NOT_FOUND);

            }

            return ResponseEntity.ok(stockId);

        } catch (NumberFormatException e) {

           return ApiErrorHandling.customApiError("ID Must be a number " + id, HttpStatus.BAD_REQUEST);

        }
        catch (Exception e) {

           return ApiErrorHandling.genericApiError(e);

        }
    }

    @GetMapping("/stocksymbol/{symbol}")
    private ResponseEntity<?> getStockSymbol (@PathVariable String symbol) {

        try {

            Optional<Overview> stockId = overviewRepository.findBySymbol(symbol);

            if (stockId == null) {

                return ApiErrorHandling.customApiError("did not match any overview", HttpStatus.NOT_FOUND);

            }

            return ResponseEntity.ok(stockId);

        } catch (NumberFormatException e) {

            return ApiErrorHandling.customApiError("ID Must be a number " + symbol, HttpStatus.BAD_REQUEST);

        }
        catch (Exception e) {

            return ApiErrorHandling.genericApiError(e);

        }

    }

    @GetMapping("/stockexchange/{exchange}")
    private ResponseEntity<?> getStockExchange (@PathVariable String exchange) {

        try {

            List<Overview> stockId = overviewRepository.findByExchange(exchange);

            if (stockId == null) {

                return ApiErrorHandling.customApiError("did not match any overview", HttpStatus.NOT_FOUND);

            }

            return ResponseEntity.ok(stockId);

        } catch (NumberFormatException e) {

            return ApiErrorHandling.customApiError("ID Must be a number " + exchange, HttpStatus.BAD_REQUEST);

        }
        catch (Exception e) {

            return ApiErrorHandling.genericApiError(e);

        }

    }

    @GetMapping("/stocksector/{sector}")
    private ResponseEntity<?> getStockSector (@PathVariable String sector) {

        try {

            List<Overview> stockId = overviewRepository.findBySector(sector);

            if (stockId == null) {

                return ApiErrorHandling.customApiError("did not match any overview", HttpStatus.NOT_FOUND);

            }

            return ResponseEntity.ok(stockId);

        } catch (NumberFormatException e) {

            return ApiErrorHandling.customApiError("ID Must be a number " + sector, HttpStatus.BAD_REQUEST);

        }
        catch (Exception e) {

            return ApiErrorHandling.genericApiError(e);

        }

    }

    @DeleteMapping("/all")
    private ResponseEntity<?> deleteAllStockData () {
        try {

            Long deleteStockData= overviewRepository.count();

            if (deleteStockData == 0) {

                return ResponseEntity.ok("No Stock To Delete");

            }

            overviewRepository.deleteAll();

            return ResponseEntity.ok("Deleted all stocks: " + deleteStockData);

        } catch (HttpClientErrorException e) {

            return ApiErrorHandling.customApiError(e.getMessage(), e.getStatusCode());

        }

        catch (Exception e) {

            return ApiErrorHandling.genericApiError(e);

        }
    }

    @DeleteMapping("/stockid/{id}")
    private ResponseEntity<?> deleteStockId (@PathVariable String id) {
        try  {

            long stockIdValue = Long.parseLong(id);

            Optional<Overview> stockId = overviewRepository.findById(Long.parseLong(id));

        if (stockId.isEmpty()) {

            return ApiErrorHandling.customApiError("did not match any overview", HttpStatus.NOT_FOUND);

        }

        overviewRepository.deleteById(stockIdValue);

        return ResponseEntity.ok(stockId);

    } catch (NumberFormatException e) {

        return ApiErrorHandling.customApiError("ID Must be a number " + id, HttpStatus.BAD_REQUEST);

    } catch (Exception e) {

            return ApiErrorHandling.genericApiError(e);

        }
    }
}

