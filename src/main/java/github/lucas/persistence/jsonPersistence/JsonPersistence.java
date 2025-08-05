package github.lucas.persistence.jsonPersistence;

//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import github.lucas.core.pass_generation.Credential;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.Map;
//
// First implementation of database system
//public class JsonPersistence {
//
//    private static final ObjectMapper mapper = new ObjectMapper();
//
//    // Unsafe simple save/load functions
//    public static void saveToFile(Map<String, Credential> map, File file) throws IOException {
//        mapper.writerWithDefaultPrettyPrinter().writeValue(file, map);
//    }
//
//    public static Map<String, Credential> loadFromFile(File file) throws IOException {
//        if (!file.exists()) {
//            return new HashMap<>();
//        }
//        return mapper.readValue(file, new TypeReference<>() {
//        });
//    }
//}
