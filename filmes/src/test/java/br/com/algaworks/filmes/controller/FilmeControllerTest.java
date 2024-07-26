package br.com.algaworks.filmes.controller;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.standaloneSetup;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;

import br.com.algaworks.filmes.model.Filme;
import br.com.algaworks.filmes.service.FilmeService;
import io.restassured.http.ContentType;

// @WebMvcTest is used to test the controller layer within a web context
@WebMvcTest
public class FilmeControllerTest {

  // @Autowired is used to inject the controller layer. Uses the real implementation.
  @Autowired private FilmeController filmeController;

  // @MockBean is used to mock the service layer.
  // Required because of the standaloneSetup() method
  @MockBean private FilmeService filmeService;

  @BeforeEach
  public void setup() {
    // RestAssuredMockMvc.standaloneSetup() is used to test the controller layer without loading the
    // entire application context.
    // It injects the controller layer with the instances that the object have (@MockBean) and
    // mocks the service layer.
    standaloneSetup(this.filmeController);
  }

  // @Test indicates to the JUnit that the method is a test method
  @Test
  public void deveRetornarSucesso_QuandoBuscarFilme() {

    // Mockito.when() is used to configure the mock behavior
    when(this.filmeService.obterFilme(1L))
        .thenReturn(new Filme(1L, "O Poderoso Chefão", "Sem descrição"));

    // given() is used to start the request configuration
    given()
        // accept() is used to set the request header
        .accept(ContentType.JSON)
        // when() is used to perform the request
        .when()
        // get() is used to set the request method and URI
        .get("/filmes/{codigo}", 1L)
        // then() is used to perform the response verification
        .then()
        // statusCode() is used to verify the response status code
        .statusCode(HttpStatus.OK.value());
  }

  @Test
  public void deveRetornarNaoEncontrado_QuandoBuscarFilme() {

    when(this.filmeService.obterFilme(5L)).thenReturn(null);

    given()
        .accept(ContentType.JSON)
        .when()
        .get("/filmes/{codigo}", 5L)
        .then()
        .statusCode(HttpStatus.NOT_FOUND.value());
  }

  @Test
  public void deveRetornarBadRequest_QuandoBuscarFilme() {

    given()
        .accept(ContentType.JSON)
        .when()
        .get("/filmes/{codigo}", -1L)
        .then()
        .statusCode(HttpStatus.BAD_REQUEST.value());

    verify(this.filmeService, never()).obterFilme(-1L);
  }
}
