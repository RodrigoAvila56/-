package com.project.Controller;

import com.project.Controller.dto.CreateUserDTO;
import com.project.models.ERole;
import com.project.models.RoleEntity;
import com.project.models.UserEntity;
import com.project.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/principal")
@Tag(name = "Principal", description = "Controller of endpoints for Principal")
public class PrincipalController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Operation(
            summary = "Crear un nuevo Usuario",
            description = "Registra un nuevo usuario en el sistema asignándole roles específicos.",
            tags = {"Authentication", "User"},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Objeto JSON que representa el usuario a crear",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = CreateUserDTO.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Usuario creado exitosamente",
                            content = @Content(
                                    schema = @Schema(implementation = UserEntity.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Error en los datos enviados",
                            content = @Content
                    )
            }
    )
    @PostMapping("/createUser")
    public ResponseEntity<?>createUser(@RequestBody CreateUserDTO userDTO){
        Set<RoleEntity> roles = userDTO.getRoles().stream().map( role -> RoleEntity.builder().name(ERole.valueOf(role))
                .build()).collect(Collectors.toSet());

        UserEntity userEntity = UserEntity.builder()
                .username(userDTO.getUsername())
                .email(userDTO.getEmail())
                .password(passwordEncoder.encode(userDTO.getPassword()))
                .roles(roles)
                .build();
        userRepository.save(userEntity);
        return ResponseEntity.ok(userEntity);
    }


    @Operation(
            summary = "Eliminar un Usuario",
            description = "Elimina un usuario del sistema utilizando su ID.",
            tags = {"Authentication", "User"},
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "ID del usuario a eliminar",
                            required = true,
                            schema = @Schema(type = "string")
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Usuario eliminado exitosamente",
                            content = @Content(
                                    schema = @Schema(type = "string")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "ID inválido o error en la solicitud",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Usuario no encontrado",
                            content = @Content
                    )
            }
    )
    @DeleteMapping("/deleteUser")
    public String deleteUser(@RequestParam String id){
        userRepository.deleteById(Long.valueOf(id));
        return "Se ha borrado el usuario con id".concat(id);
    }
}