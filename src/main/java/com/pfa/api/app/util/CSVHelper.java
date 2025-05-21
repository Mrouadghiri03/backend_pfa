package com.pfa.api.app.util;

import com.pfa.api.app.entity.user.Role;
import com.pfa.api.app.entity.user.RoleName;
import com.pfa.api.app.entity.user.User;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class CSVHelper {

    public static String TYPE = "text/csv";
    static String[] HEADERS = {
            "first_name",
            "last_name",
            "email",
            "cin",
            "inscription_number",
            "password",
            "reset_code",
            "profile_image"
    };

    public static boolean hasCSVFormat(MultipartFile file) {
        return TYPE.equals(file.getContentType());
    }


    public static List<User> csvToStudents(InputStream is,PasswordEncoder passwordEncoder) throws IOException {

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            // Modification ici pour utiliser la ; comme délimiteur
            //ou bien des fois on utilse la tabulation ca depods du fichier csv
            CSVFormat format = CSVFormat.DEFAULT
                    .withDelimiter(';') // Utilisation de la ; // meme on peut utiliser tabulation si onest besoin
                    .withFirstRecordAsHeader()
                    .withIgnoreHeaderCase()
                    .withTrim()
                    .withAllowMissingColumnNames();

            // Création et attribution du rôle étudiant
            Role studentRole = new Role("ROLE_STUDENT");
            List<Role> roles = new ArrayList<>();
            roles.add(studentRole);



            CSVParser parser = new CSVParser(reader, format);
            List<User> students = new ArrayList<>();

            for (CSVRecord record : parser) {
                try {
                    User user = new User();
                    user.setFirstName(record.get("first_name").trim());
                    user.setLastName(record.get("last_name").trim());
                    user.setEmail(record.get("email").trim());
                    user.setCin(record.get("cin").trim());
                    user.setInscriptionNumber(record.get("inscription_number").trim());
                    user.setPassword(passwordEncoder.encode(record.get("password").trim()));
                    //passwordEncoder.encode(request.getPassword())
                 //car le role prends string et user prends un list des string
                    user.setRoles(roles);

                    user.setEnabled(true);
                    user.setResetCode(record.get("reset_code").trim());
                    user.setProfileImage(record.get("profile_image").trim());
                    user.setPasswordChanged(false);

                    students.add(user);

                } catch (Exception e) {
                    System.err.println("Error parsing record: " + record);
                    throw new RuntimeException("Error parsing CSV record: " + record, e);
                }
            }
            return students;
        } catch (Exception e) {
            System.err.println("Error in csvToStudents: " + e.getMessage());
            throw e;
        }
    }
}

