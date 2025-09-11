/*
 * HTTP wrapper for COBOL MongoDB REST API integration
 * Provides simple HTTP request functionality using libcurl
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <curl/curl.h>
#include <json-c/json.h>

/* Response structure for curl */
struct http_response {
    char *memory;
    size_t size;
};

/* Callback function to write response data */
static size_t WriteMemoryCallback(void *contents, size_t size, size_t nmemb, void *userp) {
    size_t realsize = size * nmemb;
    struct http_response *mem = (struct http_response *)userp;
    
    char *ptr = realloc(mem->memory, mem->size + realsize + 1);
    if (!ptr) {
        printf("Not enough memory (realloc returned NULL)\n");
        return 0;
    }
    
    mem->memory = ptr;
    memcpy(&(mem->memory[mem->size]), contents, realsize);
    mem->size += realsize;
    mem->memory[mem->size] = 0;
    
    return realsize;
}

/* HTTP request function called from COBOL */
void http_request(char *request_data, char *response_data) {
    CURL *curl;
    CURLcode res;
    struct http_response chunk;
    
    /* Parse request data (simplified for demo) */
    char *url = request_data;
    char *headers = request_data + 1024;
    char *body = request_data + 1536;
    char *method = request_data + 3584;
    
    chunk.memory = malloc(1);
    chunk.size = 0;
    
    curl = curl_easy_init();
    if (curl) {
        curl_easy_setopt(curl, CURLOPT_URL, url);
        curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, WriteMemoryCallback);
        curl_easy_setopt(curl, CURLOPT_WRITEDATA, (void *)&chunk);
        
        /* Set headers */
        struct curl_slist *header_list = NULL;
        header_list = curl_slist_append(header_list, headers);
        curl_easy_setopt(curl, CURLOPT_HTTPHEADER, header_list);
        
        /* Set method and body for POST */
        if (strncmp(method, "POST", 4) == 0) {
            curl_easy_setopt(curl, CURLOPT_POSTFIELDS, body);
        }
        
        res = curl_easy_perform(curl);
        
        if (res == CURLE_OK) {
            long response_code;
            curl_easy_getinfo(curl, CURLINFO_RESPONSE_CODE, &response_code);
            
            /* Copy response back to COBOL */
            memcpy(response_data, &response_code, sizeof(long));
            strncpy(response_data + 8, chunk.memory, 4096);
            memcpy(response_data + 4104, &chunk.size, sizeof(size_t));
        }
        
        curl_slist_free_all(header_list);
        curl_easy_cleanup(curl);
    }
    
    if (chunk.memory) {
        free(chunk.memory);
    }
}

/* JSON parsing function for account records */
void parse_json_accounts(char *json_str, char *account_records, int *num_accounts) {
    json_object *root, *documents, *doc, *field;
    int i, count = 0;
    
    root = json_tokener_parse(json_str);
    if (!root) return;
    
    if (json_object_object_get_ex(root, "documents", &documents)) {
        int array_len = json_object_array_length(documents);
        
        for (i = 0; i < array_len && count < 100; i++) {
            doc = json_object_array_get_idx(documents, i);
            if (!doc) continue;
            
            /* Parse each field and copy to COBOL record structure */
            char *record_ptr = account_records + (count * 74); /* 74 bytes per record */
            
            /* ID field */
            if (json_object_object_get_ex(doc, "_id", &field)) {
                int id = json_object_get_int(field);
                sprintf(record_ptr, "%05d", id);
            }
            
            /* First name */
            if (json_object_object_get_ex(doc, "first_name", &field)) {
                const char *str = json_object_get_string(field);
                strncpy(record_ptr + 5, str, 8);
            }
            
            /* Last name */
            if (json_object_object_get_ex(doc, "last_name", &field)) {
                const char *str = json_object_get_string(field);
                strncpy(record_ptr + 13, str, 8);
            }
            
            /* Phone */
            if (json_object_object_get_ex(doc, "phone", &field)) {
                const char *str = json_object_get_string(field);
                strncpy(record_ptr + 21, str, 10);
            }
            
            /* Address */
            if (json_object_object_get_ex(doc, "address", &field)) {
                const char *str = json_object_get_string(field);
                strncpy(record_ptr + 31, str, 22);
            }
            
            /* Is enabled */
            if (json_object_object_get_ex(doc, "is_enabled", &field)) {
                const char *str = json_object_get_string(field);
                record_ptr[53] = str[0];
            }
            
            /* Create date */
            if (json_object_object_get_ex(doc, "create_dt", &field)) {
                const char *str = json_object_get_string(field);
                strncpy(record_ptr + 54, str, 20);
            }
            
            /* Mod date */
            if (json_object_object_get_ex(doc, "mod_dt", &field)) {
                const char *str = json_object_get_string(field);
                strncpy(record_ptr + 74, str, 20);
            }
            
            count++;
        }
    }
    
    *num_accounts = count;
    json_object_put(root);
}
