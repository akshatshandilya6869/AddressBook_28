package com.Address.AddressBook.Repository;




import com.Address.AddressBook.Model.Contact;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactRepository extends JpaRepository<Contact, Long> {}