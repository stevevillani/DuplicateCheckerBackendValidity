package com.villani.checkDuplicates.service;

import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;
import com.villani.checkDuplicates.domain.Person;
import com.villani.checkDuplicates.domain.PersonWrapper;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DuplicatesService {

    //takes in a csv file
    //outputs a PersonWrapper with the separated duplicates and uniques
    public PersonWrapper checkDuplicates(MultipartFile file) throws Exception {

        //converts csv to list of Persons
        List<Person> people = csvToPojoList(file);
        Map<String, List<Person>> map = new HashMap<>();
        for (Person person : people) {

            //if the Person does not have any state info
            if (person.getStateLong() == null || person.getStateLong().equals("")) {
                //run through every Person in the map to check for duplicates
                for (List<Person> currentState : map.values()) {
                    for (Person currentPerson : currentState) {
                        if (isDuplicate(person, currentPerson)) {
                            currentPerson.setDuplicate(person);
                            break;
                        }
                    }
                }
            } else {
                //puts People into map based on the State they live in

                //if the state already exists in the map
                if (map.containsKey(person.getStateLong())) {
                    boolean isDupp = false;
                    //goes through each person in the same state to check for potential duplicates
                    for (Person personInSameState : map.get(person.getStateLong())) {
                        if (isDuplicate(person, personInSameState)) {
                            personInSameState.setDuplicate(person);
                            isDupp = true;
                            break;
                        }
                    }
                    if (!isDupp) {
                        map.get(person.getStateLong()).add(person);
                    }
                } else {
                    //if the state does not already exit in the map
                    List<Person> newPersonList = new ArrayList();
                    newPersonList.add(person);
                    map.put(person.getStateLong(), newPersonList);
                }
            }

        }

        List<Person> duplicates = new ArrayList<>();
        List<Person> uniques = new ArrayList<>();
        PersonWrapper personWrapper = new PersonWrapper();

        //separates map into their respective lists
        for (List<Person> currentState : map.values()) {
            for (Person currentPerson : currentState) {
                if (currentPerson.getDuplicate() == null) {
                    uniques.add(currentPerson);
                } else {
                    duplicates.add(currentPerson);
                }
            }
        }
        personWrapper.setDuplicates(duplicates);
        personWrapper.setUniques(uniques);
        return personWrapper;
    }

    //takes in two Persons
    //outputs a boolean. True if Persons are duplicates, false if not
    private boolean isDuplicate(Person person, Person personInSameState) {

        //gets the distance between the two people
        int dist = distance(person.getFirst(), personInSameState.getFirst()) +
                distance(person.getLast(), personInSameState.getLast() +
                        distance(person.getCompany(), personInSameState.getCompany() +
                                distance(person.getEmail(), personInSameState.getEmail() +
                                        distance(person.getAddress1(), personInSameState.getAddress1()))) +
                        distance(person.getAddress2(), personInSameState.getAddress2()) +
                        distance(person.getZip(), personInSameState.getZip()) +
                        distance(person.getCity(), personInSameState.getCity()) +
                        distance(person.getStateLong(), personInSameState.getStateLong()) +
                        distance(person.getState(), personInSameState.getState() +
                                distance(person.getPhone(), personInSameState.getPhone())));
        //if distance between two Persons is less than 10, they can be considered duplicates
        if (dist < 10) {
            return true;
        }
        return false;
    }

    //takes in two Strings
    //outputs the Levenshtein Distance (int) between the two strings
    private int distance(String personAttribute, String personInSameStateAttr) {
        //uses appache commons LevenshteinDistance to find the difference between two strings
        LevenshteinDistance dist = new LevenshteinDistance();
        return dist.apply(personAttribute, personInSameStateAttr);
    }

    //takes in a multipart file
    //outputs List<Person> of all the 'people' in the file
    private List<Person> csvToPojoList(MultipartFile file) throws Exception {

        //map used to connect csv headers to pojo fields
        Map<String, String> mapping = new HashMap<>();
        mapping.put("first_name", "first");
        mapping.put("last_name", "last");
        mapping.put("company", "company");
        mapping.put("email", "email");
        mapping.put("address1", "address1");
        mapping.put("address2", "address2");
        mapping.put("zip", "zip");
        mapping.put("city", "city");
        mapping.put("state_long", "stateLong");
        mapping.put("state", "state");
        mapping.put("phone", "phone");

        //sets the type of pojo to convert the csv rows to
        HeaderColumnNameTranslateMappingStrategy<Person> strategy = new HeaderColumnNameTranslateMappingStrategy<Person>();
        strategy.setType(Person.class);
        strategy.setColumnMapping(mapping);

        CSVReader csvReader = null;
        //convers csv to List<Person> using opencsv
        try {
            File convFile = new File(file.getOriginalFilename());
            convFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(convFile);
            fos.write(file.getBytes());
            fos.close();
            csvReader = new CSVReader(new FileReader(convFile));
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        CsvToBean csvToBean = new CsvToBean();

        List<Person> listOfPeople = csvToBean.parse(strategy, csvReader);

        return listOfPeople;
    }
}
