package ar.com.unla.api.controllers;

import ar.com.unla.api.dtos.request.ExcelDTO;
import ar.com.unla.api.dtos.response.AlumnosFinalDTO;
import ar.com.unla.api.models.database.UsuarioExamenFinal;
import ar.com.unla.api.models.response.ApplicationResponse;
import ar.com.unla.api.models.response.ErrorResponse;
import ar.com.unla.api.models.swagger.usuarioexamenfinal.SwaggerAlumnosFinalOk;
import ar.com.unla.api.models.swagger.usuarioexamenfinal.SwaggerUsuarioFinalOk;
import ar.com.unla.api.services.UsuarioExamenFinalService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import org.apache.commons.codec.DecoderException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "Usuario-ExamenFinal controller", description = "CRUD UsuarioExamenFinal")
@Validated
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/usuarios-examenes-finales")
public class UsuarioExamenFinalController {

    @Autowired
    private UsuarioExamenFinalService usuarioExamenFinalService;


    @GetMapping(path = "/alumnos")
    @ApiOperation(value = "Se encarga de buscar una lista de alumnos relacionados a un examen "
            + "final filtrado por la materia en cuestion")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Alumnos por examen final encontrados",
                            response = SwaggerAlumnosFinalOk.class),
                    @ApiResponse(code = 400, message = "Request incorrecta al buscar una lista de"
                            + " alumnos por examen final", response = ErrorResponse.class),
                    @ApiResponse(code = 500, message =
                            "Error interno al buscar una lista de alumnos por examen final",
                            response = ErrorResponse.class)
            }
    )
    @ResponseStatus(HttpStatus.OK)
    public ApplicationResponse<List<AlumnosFinalDTO>> getStudentsByFinalExam(
            @RequestParam(name = "idMateria")
            @NotNull(message = "El parámetro idMateria no esta informado.")
            @ApiParam(required = true) Long idMateria) {
        return new ApplicationResponse<>(
                usuarioExamenFinalService.findUsersByFinalExam(idMateria), null);
    }

    @PutMapping(path = "/calificaciones")
    @ApiOperation(value = "Se encarga de actualizar la calificación de un alumno en un examen "
            + "final")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Calificación de examen final actualizada"
                            , response = SwaggerUsuarioFinalOk.class),
                    @ApiResponse(code = 400, message =
                            "Request incorrecta al actualizar la calificación de un examen final",
                            response = ErrorResponse.class),
                    @ApiResponse(code = 500, message =
                            "Error interno al actualizar la calificación de un examen final",
                            response = ErrorResponse.class)
            }
    )
    @ResponseStatus(HttpStatus.OK)
    public ApplicationResponse<UsuarioExamenFinal> updateQualification(
            @RequestParam(name = "idUsuarioExamenFinal")
            @NotNull(message = "El parámetro idUsuarioExamenFinal no esta informado.")
            @ApiParam(required = true) Long id,
            @RequestParam(name = "calificacion")
            @NotNull(message = "El parámetro calificación no esta informado.")
            @Digits(integer = 2, fraction = 2, message =
                    "El parámetro calificación puede tener {integer} cifras enteras y {fraction} "
                            + "cifras decimales como máximo.")
            @Max(value = 10, message = "El parametro calificación no puede ser mayor a {value}")
            @Min(value = 0, message = "El parametro calificación no puede ser menor a {value}}")
            @ApiParam(required = true) float calificacion) {
        return new ApplicationResponse<>(
                usuarioExamenFinalService.updateQualification(id, calificacion), null);
    }

    @GetMapping("/notas-excel")
    @ApiOperation(value = "Se encarga de generar un excel con el listado de alumnos de un final")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Excel generado correctamente"),
                    @ApiResponse(code = 400, message =
                            "Request incorrecta al generar un excel con la lista de alumnos",
                            response = ErrorResponse.class),
                    @ApiResponse(code = 500, message =
                            "Error al intentar generar un excel con la lista de alumnos",
                            response = ErrorResponse.class)
            }
    )
    public void qualificationExcelExport(HttpServletResponse response,
            @RequestParam(name = "idMateria")
            @NotNull(message = "El parámetro idMateria no esta informado.")
            @ApiParam(required = true) Long idMateria)
            throws IOException, DecoderException {

        usuarioExamenFinalService.exportToExcel(response, idMateria);
    }

    @PutMapping("/notas-excel")
    @ApiOperation(value = "Se encarga obtener los datos de un excel con la lista de alumnos de "
            + "un examen final y actualizar las notas de esos alumnos en la base de datos")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Calificaciones actualizadas correctamente"),
                    @ApiResponse(code = 400, message =
                            "Request incorrecta al leer un excel con la lista de alumnos",
                            response = ErrorResponse.class),
                    @ApiResponse(code = 500, message =
                            "Error al intentar al leer un excel con la lista de alumnos",
                            response = ErrorResponse.class)
            }
    )
    @ResponseStatus(HttpStatus.OK)
    public ApplicationResponse<String> qualificationExcelImport(
            @Valid @RequestBody ExcelDTO excelDTO) {
        return new ApplicationResponse<>(
                usuarioExamenFinalService.importByExcel(excelDTO), null);
    }


}
