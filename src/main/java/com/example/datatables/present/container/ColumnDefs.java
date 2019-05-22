package com.example.datatables.present.container;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class ColumnDefs {

    private int[] targets;
    private boolean orderable;

    public ColumnDefs() {
        this.targets = new int[] { 0 };
        this.orderable = false;
    }
}
