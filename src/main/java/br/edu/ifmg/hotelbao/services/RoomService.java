package br.edu.ifmg.hotelbao.services;

import br.edu.ifmg.hotelbao.dtos.RoomDTO;
import br.edu.ifmg.hotelbao.entities.Room;
import br.edu.ifmg.hotelbao.repositories.RoomRepository;
import br.edu.ifmg.hotelbao.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;

    @Transactional(readOnly = true)
    public Page<RoomDTO> findAll(Pageable pageable) {
        return roomRepository.findByIsActiveTrue(pageable).map(RoomDTO::new);
    }

    @Transactional(readOnly = true)
    public RoomDTO findById(Long id) {
        Room entity = roomRepository.findByIdAndIsActiveTrue(id).orElseThrow(() -> new ResourceNotFoundException("[!] -> Room not found or inactive!"));
        return new RoomDTO(entity);
    }

    @Transactional
    public RoomDTO insert(RoomDTO dto) {
        Room entity = new Room();
        copyDTOToEntity(dto, entity);
        entity = roomRepository.save(entity);
        return new RoomDTO(entity);
    }

    @Transactional
    public RoomDTO update(Long id, RoomDTO dto) {
        Room entity = roomRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("[!] -> Room not found or inactive!"));

        copyDTOToEntity(dto, entity);
        entity = roomRepository.save(entity);
        return new RoomDTO(entity);
    }

    @Transactional
    public void delete(Long id) {
        Room room = roomRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("[!] -> Room not found or inactive!"));
        room.setIsActive(false);
        roomRepository.save(room);
    }

    private void copyDTOToEntity(RoomDTO dto, Room entity) {
        if (dto.getDescription() != null) entity.setDescription(dto.getDescription());
        if (dto.getPrice() != null) entity.setPrice(dto.getPrice());
        if (dto.getImageUrl() != null) entity.setImageUrl(dto.getImageUrl());
        entity.setIsActive(true);
    }

}
