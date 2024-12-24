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

import com.project.Controller.dto.ProductDTO;
import com.project.models.Product;
import com.project.service.IProductService;

@RestController
@RequestMapping("api/product")
@Tag(name = "Product", description = "Controller of endpoints for Product")
public class ProductController {

	@Autowired
	private IProductService productService;


	@Operation(
			summary = "Buscar un Producto por ID",
			description = "Obtiene la información de un Producto existente identificado por su ID. Accesible para usuarios con roles USER, ADMIN o INVITED.",
			tags = {"Authentication", "Product"},
			parameters = {
					@Parameter(
							name = "id",
							description = "ID del Producto a buscar",
							required = true,
							schema = @Schema(type = "integer")
					)
			},
			responses = {
					@ApiResponse(
							responseCode = "200",
							description = "Producto encontrado exitosamente",
							content = @Content(
									mediaType = "application/json",
									schema = @Schema(implementation = ProductDTO.class)
							)
					),
					@ApiResponse(
							responseCode = "404",
							description = "Producto no encontrado",
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
	       Optional<Product> productOptional = productService.findById(id);
	        if (productOptional.isPresent()){
	           Product product = productOptional.get();
	            ProductDTO productDTO = ProductDTO.builder()
	                    .id(product.getId())
	                    .name(product.getName())
	                    .price(product.getPrice())
	                    .maker(product.getMaker())
	                    .build();
	            return ResponseEntity.ok(productDTO);
	        }
	        return ResponseEntity.notFound().build();
	    }


	@Operation(
			summary = "Listar todos los Productos",
			description = "Obtiene la lista de todos los Productos registrados. Accesible para usuarios con roles USER, ADMIN o INVITED.",
			tags = {"Authentication", "Product"},
			responses = {
					@ApiResponse(
							responseCode = "200",
							description = "Lista de Productos obtenida exitosamente",
							content = @Content(
									mediaType = "application/json",
									array = @ArraySchema(schema = @Schema(implementation = ProductDTO.class))
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
	    	List<ProductDTO> prducList = productService.findAll().stream().map(product -> ProductDTO.builder()
	    			.id(product.getId())
	    			.name(product.getName())
	    			.price(product.getPrice())
	    			.maker(product.getMaker())
	    			.build()).toList();
	    	return ResponseEntity.ok(prducList);
	    }


	@Operation(
			summary = "Guardar un Producto",
			description = "Registra un nuevo Producto en el sistema. Solo accesible para usuarios con rol ADMIN.",
			tags = {"Authentication", "Product"},
			requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
					description = "Datos del Producto a registrar",
					required = true,
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ProductDTO.class)
					)
			),
			responses = {
					@ApiResponse(
							responseCode = "201",
							description = "Producto registrado exitosamente",
							content = @Content
					),
					@ApiResponse(
							responseCode = "400",
							description = "Error en la solicitud. Los datos proporcionados son inválidos",
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
	    public ResponseEntity<?> save(@RequestBody ProductDTO productDTO) throws URISyntaxException{
		    if(productDTO.getName().isBlank() || productDTO.getPrice() == null || productDTO.getMaker() == null) {
	    		return ResponseEntity.badRequest().build();
	    	}
	    	
	    	Product product = Product.builder()
	    			.name(productDTO.getName())
	    			.price(productDTO.getPrice())
	    			.maker(productDTO.getMaker())
	    			.build();
	    	
	    	productService.save(product);
	    	return ResponseEntity.created(new URI("/api/product/save")).build();
	    }


	@Operation(
			summary = "Actualizar un Producto",
			description = "Actualiza la información de un Producto existente identificado por su ID. Solo accesible para usuarios con rol ADMIN.",
			tags = {"Authentication", "Product"},
			parameters = {
					@Parameter(
							name = "id",
							description = "ID del Producto a actualizar",
							required = true,
							schema = @Schema(type = "integer")
					)
			},
			requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
					description = "Datos actualizados del Producto",
					required = true,
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ProductDTO.class)
					)
			),
			responses = {
					@ApiResponse(
							responseCode = "200",
							description = "Producto actualizado exitosamente",
							content = @Content(schema = @Schema(type = "string"))
					),
					@ApiResponse(
							responseCode = "404",
							description = "Producto no encontrado",
							content = @Content
					),
					@ApiResponse(
							responseCode = "403",
							description = "Acceso denegado. El usuario no tiene los permisos necesarios",
							content = @Content
					)
			}
	)
	    @PutMapping("update/{id}")
		@PreAuthorize("hasRole('ADMIN')")
	    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody ProductDTO productDTO){
	    	Optional<Product> productOptional = productService.findById(id);
	    	if(productOptional.isPresent()) {
	    		Product product = productOptional.get();
	    		product.setName(productDTO.getName());
	    		product.setPrice(productDTO.getPrice());
	    		product.setMaker(productDTO.getMaker());
	    		productService.save(product);
	    		return ResponseEntity.ok("Registro Actualizado");
	    	}
	    	return ResponseEntity.notFound().build();
	    }


	@Operation(
			summary = "Eliminar un Producto",
			description = "Elimina un Producto existente identificado por su ID. Solo accesible para usuarios con rol ADMIN.",
			tags = {"Authentication", "Product"},
			parameters = {
					@Parameter(
							name = "id",
							description = "ID del Producto a eliminar",
							required = true,
							schema = @Schema(type = "integer")
					)
			},
			responses = {
					@ApiResponse(
							responseCode = "200",
							description = "Producto eliminado exitosamente",
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
	    		productService.deleteById(id);
	    		return ResponseEntity.ok("Registro Eliminado");
	    	}
	    	return ResponseEntity.badRequest().build();
	    }
}