package com.example.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "responsables")
public class Responsable extends Employe {

    @OneToMany(mappedBy = "responsable")
    private List<Employe> employesEncadres = new ArrayList<>();
}
