package com.project.Controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.Controller.dto.MakerDTO;
import com.project.models.Maker;
import com.project.service.IMakerService;

@RestController
@RequestMapping("/api/maker")

@Tag(name = "Maker", description = "Controller of endpoints for Maker")
public class MakerController {

    @Autowired
    private IMakerService makerService;


    @Operation(
            summary = "Buscar Maker por ID",
            description = "Permite recuperar los detalles de un Maker especificando su ID. Solo accesible para usuarios con roles autorizados.",
            tags = {"Authentication", "Maker"},
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "El ID del Maker a buscar",
                            required = true,
                            example = "1"
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Maker encontrado exitosamente",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = MakerDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "No se encontró un Maker con el ID especificado",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Acceso denegado. El usuario no tiene los permisos necesarios",
                            content = @Content
                    )
            }
    )
    @GetMapping("/find/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN','INVITED')")
    public ResponseEntity<?> findById(@PathVariable Long id){
        Optional<Maker>  makerOptional = makerService.findById(id);

        if (makerOptional.isPresent()){
            Maker maker = makerOptional.get();

            MakerDTO makerDTO = MakerDTO.builder()
                    .id(maker.getId())
                    .name(maker.getName())
                    .productList(maker.getProductList())
                    .build();
            return ResponseEntity.ok(makerDTO);
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(
            summary = "Listar todos los Makers",
            description = "Recupera una lista de todos los Makers disponibles en el sistema. Solo accesible para usuarios con roles autorizados.",
            tags = {"Authentication", "Maker"},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Lista de Makers recuperada exitosamente",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = MakerDTO.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Acceso denegado. El usuario no tiene los permisos necesarios",
                            content = @Content
                    )
            }
    )
    @GetMapping("/findAll")
    @PreAuthorize("hasAnyRole('USER','ADMIN','INVITED')")
    public ResponseEntity<?> findAll(){
    	List<MakerDTO> makerkist = makerService.findAll().stream().map(maker -> MakerDTO.builder().id(maker.getId())
    			.name(maker.getName())
    			.productList(maker.getProductList())
    			.build()).toList();
    	return ResponseEntity.ok(makerkist);
    }


    @Operation(
            summary = "Guardar un Maker",
            description = "Permite crear un nuevo Maker en el sistema. Solo accesible para usuarios con rol ADMIN.",
            tags = {"Authentication", "Maker"},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del Maker a guardar",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MakerDTO.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Maker creado exitosamente",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Error en los datos proporcionados",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Acceso denegado. El usuario no tiene los permisos necesarios",
                            content = @Content
                    )
            }
    )
    @PostMapping("/save")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> save(@RequestBody MakerDTO makerDTO) throws URISyntaxException{
        if(makerDTO.getName().isBlank()) {
    		return ResponseEntity.badRequest().build();
    	}
    	makerService.save(Maker.builder().name(makerDTO.getName()).build());
    	return ResponseEntity.created(new URI("/api/maker/save")).build();
    }

    @Operation(
            summary = "Actualizar un Maker",
            description = "Actualiza la información de un Maker existente identificado por su ID. Solo accesible para usuarios con rol ADMIN.",
            tags = {"Authentication", "Maker"},
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "ID del Maker a actualizar",
                            required = true,
                            schema = @Schema(type = "integer")
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos actualizados del Maker",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MakerDTO.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Maker actualizado exitosamente",
                            content = @Content(schema = @Schema(type = "string"))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Maker no encontrado",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Acceso denegado. El usuario no tiene los permisos necesarios",
                            content = @Content
                    )
            }
    )
    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateMaker(@PathVariable Long id,@RequestBody MakerDTO makerDTO){
        Optional<Maker> makerOptional = makerService.findById(id);
    	
    	if (makerOptional.isPresent()) {
			Maker maker = makerOptional.get();
			maker.setName(makerDTO.getName());
			makerService.save(maker);
			return ResponseEntity.ok("Registro Actualizado");
		}
    	return ResponseEntity.notFound().build();
    }


    @Operation(
            summary = "Eliminar un Maker",
            description = "Elimina un Maker existente identificado por su ID. Solo accesible para usuarios con rol ADMIN.",
            tags = {"Authentication", "Maker"},
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "ID del Maker a eliminar",
                            required = true,
                            schema = @Schema(type = "integer")
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Maker eliminado exitosamente",
                            content = @Content(schema = @Schema(type = "string"))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Error en la solicitud. El ID proporcionado es inválido",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Acceso denegado. El usuario no tiene los permisos necesarios",
                            content = @Content
                    )
            }
    )
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteById(@PathVariable Long id){
    	if(id !=null) {
    		makerService.deleteById(id);
    		return ResponseEntity.ok("Registro Eliminado");
    	}
    	return ResponseEntity.badRequest().build();
    }

}