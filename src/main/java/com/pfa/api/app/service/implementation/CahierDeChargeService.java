package com.pfa.api.app.service.implementation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pfa.api.app.exception.GenerationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;



    @Service
    public class CahierDeChargeService {

        private static final Logger logger = LoggerFactory.getLogger(CahierDeChargeService.class);
        private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=%s";

        @Value("${gemini.api.key}")
        private String apiKey;

        @Value("${gemini.api.model-name}")
        private String modelName;

        private final RestTemplate restTemplate = new RestTemplate();
        private final ObjectMapper objectMapper = new ObjectMapper();

        public String generateCahierDeCharge(String requirements) throws GenerationException {
            try {
                String prompt = buildCDCPrompt(requirements);
                String responseText = callGeminiApi(prompt);
                String rawDocument = extractCDCText(responseText);
                String cleanedDocument = cleanCDCMarkdown(rawDocument);

                return formatCDCDocument(cleanedDocument);
            } catch (Exception e) {
                logger.error("CDC generation error", e);
                throw new GenerationException("Generation failed: " + e.getMessage());
            }
        }

        /*private String formatCDCDocument(String rawDocument) {
            // Supprime les sauts de ligne multiples
            String cleaned = rawDocument.replaceAll("(?m)^[ \t]*\r?\n", "");

            // Standardise les titres
            cleaned = cleaned.replace("## 1.", "## 1 -")
                    .replace("## 2.", "## 2 -");

            // Ajoute une table des matières automatique
            String toc = "## Table des Matières\n";
            if (cleaned.contains("## 1 - Introduction")) toc += "- [1. Introduction](#1-introduction)\n";
            if (cleaned.contains("## 2 - Exigences Fonctionnelles")) toc += "- [2. Exigences Fonctionnelles](#2-exigences-fonctionnelles)\n";

            return toc + "\n" + cleaned;
        }

         */
        private String formatCDCDocument(String rawDocument) {
            // Supprime les sauts de ligne multiples pour nettoyer le document
            String cleaned = rawDocument.replaceAll("(?m)^[ \\t]*\\r?\\n", "");

            // Standardise les titres pour qu'ils soient au format "## X. Titre"
            cleaned = cleaned.replace("## 1 - Introduction", "## 1. Introduction")
                    .replace("## 2 - Exigences Fonctionnelles", "## 2. Exigences Fonctionnelles")
                    .replace("## 3 - Exigences Techniques", "## 3. Exigences Techniques")
                    .replace("## 4 - Livrables", "## 4. Livrables")
                    .replace("## 5 - Planning", "## 5. Planning");

            // Re-standardise les sous-titres si nécessaire (e.g., 2.1. Module)
            cleaned = cleaned.replace("### 2 -1. Module", "### 2.1. Module"); // Example, adjust as needed based on actual AI output


            // Construit la table des matières automatique avec le format "X. Titre"
            StringBuilder tocBuilder = new StringBuilder("## Table des Matières\n");
            if (cleaned.contains("## 1. Introduction")) tocBuilder.append("- [1. Introduction](#1-introduction)\n");
            if (cleaned.contains("## 2. Exigences Fonctionnelles"))
                tocBuilder.append("- [2. Exigences Fonctionnelles](#2-exigences-fonctionnelles)\n");
            if (cleaned.contains("## 3. Exigences Techniques"))
                tocBuilder.append("- [3. Exigences Techniques](#3-exigences-techniques)\n");
            if (cleaned.contains("## 4. Livrables")) tocBuilder.append("- [4. Livrables](#4-livrables)\n");
            if (cleaned.contains("## 5. Planning")) tocBuilder.append("- [5. Planning](#5-planning)\n");


            return tocBuilder.toString() + "\n" + cleaned;
        }

        /*private String buildCDCPrompt(String requirements) {
            return """
        Génère un cahier des charges technique COMPLET en français au format Markdown strictement SANS ```markdown```.
        Structure obligatoire :

        # Titre du Projet

        ## 1. Introduction
        - **Objectif** : [maximum 3 phrases]
        - **Portée** :
          - Inclus : [liste]
          - Exclus : [liste]

        ## 2. Exigences Fonctionnelles
        ### 2.1. [Module principal]
        - [Fonctionnalité 1] : [description concise]
        - [Fonctionnalité 2] : [description concise]

        ### 2.2. [Module secondaire]
        - [...]

        ## 3. Exigences Techniques
        - **Frontend** : [technologies]
        - **Backend** : [technologies]
        - **Contraintes** : [liste]

        ## 4. Livrables
        - [Item 1] : [description]
        - [Item 2] : [description]

        ## 5. Planning
        - **Phase 1** (X semaines) : [description]
        - **Phase 2** (Y semaines) : [...]

        Texte à analyser :
        """ + requirements + """

        Règles strictes :
        - Pas de ```markdown``` dans la réponse
        - Titres en ## et ### uniquement
        - Listes à puces avec - seulement
        - Maximum 5 niveaux de profondeur
        """;
        }
        */
        private String cleanCDCMarkdown(String rawResponse) {
            // Supprime les blocs ```markdown``` s'ils existent
            return rawResponse.replace("```markdown", "")
                    .replace("```", "")
                    .trim();
        }


        private String buildCDCPrompt(String requirements) {
            return """
                    Génère un cahier des charges technique COMPLET en français au format Markdown strictement SANS ```markdown```. 
                    Structure obligatoire :
                    
                    # Titre du Projet
                    
                    ## 1. Introduction
                    - **Objectif** : [maximum 3 phrases]
                    - **Portée** : 
                      - Inclus : [liste]
                      - Exclus : [liste]
                    
                    ## 2. Exigences Fonctionnelles
                    ### 2.1. [Module principal]
                    - [Fonctionnalité 1] : [description concise]
                    - [Fonctionnalité 2] : [description concise]
                    
                    ### 2.2. [Module secondaire]
                    - [...]
                    
                    ## 3. Exigences Techniques
                    - **Frontend** : [technologies]
                    - **Backend** : [technologies]
                    - **Contraintes** : [liste]
                    
                    ## 4. Livrables
                    - [Item 1] : [description]
                    - [Item 2] : [description]
                    
                    ## 5. Planning
                    - **Phase 1** (X semaines) : [description]
                    - **Phase 2** (Y semaines) : [...]
                    
                    Texte à analyser :
                    """ + requirements + """
                    
                    Règles strictes :
                    - Pas de ```markdown``` dans la réponse.
                    - Les titres principaux (Introduction, Exigences Fonctionnelles, Exigences Techniques, Livrables, Planning) doivent être au format "## X. Titre".
                    - Les sous-titres (e.g., Module principal) doivent être au format "### X.X. Sous-titre".
                    - Listes à puces avec - seulement.
                    - Maximum 5 niveaux de profondeur.
                    """;
        }

        private String callGeminiApi(String prompt) throws GenerationException {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                Map<String, Object> requestBody = new HashMap<>();
                Map<String, Object> content = new HashMap<>();
                Map<String, Object> part = new HashMap<>();

                part.put("text", prompt);
                content.put("parts", List.of(part));
                requestBody.put("contents", List.of(content));

                HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
                String url = String.format(GEMINI_API_URL, modelName, apiKey);

                ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

                if (!response.getStatusCode().is2xxSuccessful()) {
                    throw new GenerationException("API returned status: " + response.getStatusCode());
                }

                return response.getBody();

            } catch (Exception e) {
                throw new GenerationException("Erreur API Gemini: " + e.getMessage());
            }
        }

        private String extractCDCText(String jsonResponse) throws GenerationException {
            try {
                Map<String, Object> responseMap = objectMapper.readValue(jsonResponse, Map.class);

                // Navigation dans la réponse JSON de Gemini
                List<Map<String, Object>> candidates = (List<Map<String, Object>>) responseMap.get("candidates");
                if (candidates == null || candidates.isEmpty()) {
                    throw new GenerationException("Aucun candidat dans la réponse de l'API");
                }

                Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
                List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
                if (parts == null || parts.isEmpty()) {
                    throw new GenerationException("Aucune partie dans le contenu");
                }

                String generatedText = (String) parts.get(0).get("text");
                if (generatedText == null || generatedText.isBlank()) {
                    throw new GenerationException("Le texte généré est vide");
                }

                return generatedText;

            } catch (Exception e) {
                throw new GenerationException("Erreur d'extraction du texte: " + e.getMessage());
            }
        }
    }
