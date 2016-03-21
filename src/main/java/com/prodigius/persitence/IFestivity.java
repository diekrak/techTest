package com.prodigius.persitence;

import java.util.Date;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.prodigius.model.Festivity;

public interface IFestivity  extends CrudRepository<Festivity, Long>{

	public List<Festivity> findByName(String name);

	public List<Festivity> findByPlace(String place);

	public List<Festivity> findByStartDateBetween(Date dateStartA, Date dateStartB);

}
