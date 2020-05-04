package com.ne.microservice.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceStateResponse {
    private boolean vodEnabled;
    private boolean hdEnabled;
}
