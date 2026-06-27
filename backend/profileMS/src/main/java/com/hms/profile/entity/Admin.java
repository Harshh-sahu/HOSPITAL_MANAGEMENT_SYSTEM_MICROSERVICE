package com.hms.profile.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.hms.profile.dto.AdminDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private Long userId;

    private String name;

    @Column(unique = true)
    private String email;

    private Long profilePictureId;
    private String phone;
    private String address;

    public AdminDTO toDTO() {
        return new AdminDTO(this.id, this.userId, this.name, this.email,
                this.profilePictureId, this.phone, this.address);
    }
}
