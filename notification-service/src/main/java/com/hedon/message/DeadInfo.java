package com.hedon.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author Hedon Wang
 * @create 2020-11-05 23:35
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DeadInfo implements Serializable {

    private Integer id;
    private String msg;
}
