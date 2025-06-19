package br.edu.ifmg.hotelbao.services;

import br.edu.ifmg.hotelbao.dtos.RoomDTO;
import br.edu.ifmg.hotelbao.entities.Room;
import br.edu.ifmg.hotelbao.repository.RoomRepository;
import br.edu.ifmg.hotelbao.services.exceptions.ResourceNotFound;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;

    @Transactional(readOnly = true)
    public Page<RoomDTO> findAll(Pageable pageable) {
        return roomRepository.findAll(pageable).map(RoomDTO::new);
    }

    @Transactional(readOnly = true)
    public RoomDTO findById(Long id) {
        Room entity = roomRepository.findById(id).orElseThrow(() -> new ResourceNotFound("[!] -> Room not found!"));
        return new RoomDTO(entity);
    }

    @Transactional
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    public RoomDTO insert(RoomDTO dto) {
        Room entity = new Room();
        copyDTOToEntity(dto, entity);
        entity = roomRepository.save(entity);
        return new RoomDTO(entity);
    }

    @Transactional
    public RoomDTO update(Long id, RoomDTO dto) {
        try {
            Room entity = roomRepository.getReferenceById(id);
            copyDTOToEntity(dto, entity);
            entity = roomRepository.save(entity);
            return new RoomDTO(entity);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFound("[!] -> Room not foud " + id + "!");
        }

    }

    @Transactional
    public void delete(Long id) {
        if (!roomRepository.existsById(id)) {
            throw new ResourceNotFound("[!] -> Resource not found!");
        }

        try {
            roomRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new ResourceNotFound("[!] -> Integrity Violation!");
        }
    }

    private void copyDTOToEntity(RoomDTO dto, Room entity) {
        entity.setDescription(dto.getDescription());
        entity.setPrice(dto.getPrice());
        entity.setImageUrl(dto.getImageUrl());
    }

}
