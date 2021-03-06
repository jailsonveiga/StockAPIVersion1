package com.jay.stockapiv1.repositories;

import com.jay.stockapiv1.models.Overview;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface OverviewRepository extends CrudRepository<Overview, Long> {

    public Optional<Overview> findBySymbol(String symbol);

    public List<Overview> findByExchange (String exchange);

    public List<Overview> findBySector (String sector);

    public List<Overview> findByCountry (String country);

    public Optional<Overview> findByName (String name);

    public List<Overview> findByCurrency (String currency);



}
