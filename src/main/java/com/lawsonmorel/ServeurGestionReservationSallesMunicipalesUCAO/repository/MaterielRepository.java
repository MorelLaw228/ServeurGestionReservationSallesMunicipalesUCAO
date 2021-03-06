package com.lawsonmorel.ServeurGestionReservationSallesMunicipalesUCAO.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lawsonmorel.ServeurGestionReservationSallesMunicipalesUCAO.models.Materiel;
import com.lawsonmorel.ServeurGestionReservationSallesMunicipalesUCAO.models.Salle;


@Repository
public interface MaterielRepository extends JpaRepository<Materiel,Long>{
      
	//List<Materiel> findBySalle(Salle salle,Sort sort);
       
       Optional<Materiel> findByEtat(String etat);
       
     /*  @Query("SELECT m FROM materiels m WHERE m.etat='INSTALLE' ")
   	   List<Materiel> findByMaterielInstalle();
       
       @Query("SELECT m FROM materiels m WHERE m.etat='DISPONIBLE A LA LOCATION' ")
    	List<Materiel> findByMaterielALouer();
       
       @Query("SELECT m FROM materiels m WHERE m.etat='INDSIPONIBLE' ")
   	   List<Materiel> findByMaterielIndisponible();
       
       @Query("SELECT m FROM materiels m WHERE m.etat='RESERVE' ")
     	List<Materiel> findByMaterielReserve();
       
       @Query("SELECT m FROM materiel m where m.salle_id = :salle")
	    public List<Materiel> findBySalle(@Param("idSalle") String idSalle);*/
       
	//public Optional<Materiel> findByReference(String reference);
       
       
}
