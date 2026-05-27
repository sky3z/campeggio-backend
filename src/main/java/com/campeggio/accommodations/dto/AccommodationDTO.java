package com.campeggio.accommodations.dto;

import com.campeggio.accommodations.entity.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class AccommodationDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal pricePerNight;
    private int maxCapacity;
    private String status;
    private String type;

    // Campi specifici Piazzola
    private String tipoPiazzola;
    private Double surfaceM2;
    private Boolean hasElectricity;
    private Boolean hasWater;

    // Campi specifici Bungalow
    private Integer rooms;
    private Integer beds;
    private Boolean hasBathroom;
    private Boolean hasKitchen;

    // Campi specifici PiazzolaFissa
    private BigDecimal annualFee;
    private Boolean hasPrivateEntrance;
    private String contractStart;
    private String contractEnd;
    private Long ownerId;

    public static AccommodationDTO from(Accommodation a) {
        AccommodationDTO dto = new AccommodationDTO();
        dto.setId(a.getId());
        dto.setName(a.getName());
        dto.setDescription(a.getDescription());
        dto.setPricePerNight(a.getPricePerNight());
        dto.setMaxCapacity(a.getMaxCapacity());
        dto.setStatus(a.getStatus().name());
        dto.setType(a.getType());

        if (a instanceof Piazzola p) {
            if (p.getTipoPiazzola() != null) dto.setTipoPiazzola(p.getTipoPiazzola().name());
            dto.setSurfaceM2(p.getSurfaceM2());
            dto.setHasElectricity(p.isHasElectricity());
            dto.setHasWater(p.isHasWater());
        } else if (a instanceof Bungalow b) {
            dto.setRooms(b.getRooms());
            dto.setBeds(b.getBeds());
            dto.setHasBathroom(b.isHasBathroom());
            dto.setHasKitchen(b.isHasKitchen());
        } else if (a instanceof PiazzolaFissa pf) {
            dto.setAnnualFee(pf.getAnnualFee());
            dto.setHasPrivateEntrance(pf.isHasPrivateEntrance());
            if (pf.getContractStart() != null) dto.setContractStart(pf.getContractStart().toString());
            if (pf.getContractEnd() != null) dto.setContractEnd(pf.getContractEnd().toString());
            if (pf.getOwner() != null) dto.setOwnerId(pf.getOwner().getId());
        }
        return dto;
    }
}
