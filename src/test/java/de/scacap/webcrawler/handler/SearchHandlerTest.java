package de.scacap.webcrawler.handler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.scacap.webcrawler.crawler.LinkCrawler;
import de.scacap.webcrawler.pojo.Item;
import de.scacap.webcrawler.pojo.RestResponse;

import org.assertj.core.util.Lists;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class SearchHandlerTest {

  @Mock
  private RestTemplate restTemplate;

  @Mock
  private LinkCrawler jsoupCrawler;

  private SearchHandler searchHandler;

  @Before
  public void setUp () {
    MockitoAnnotations.initMocks(this);
    searchHandler = new RestTemplateSearchHandler(restTemplate, jsoupCrawler);
    ReflectionTestUtils.setField(searchHandler, "baseUrl", "http://www.testurl.com");
    ReflectionTestUtils.setField(searchHandler, "apiKey", "1");
    ReflectionTestUtils.setField(searchHandler, "engineId", "1");
  }

  @Test
  public void handleSearchAndGetTop5Libraries() {
    final RestResponse restResponse = createDummyRestResponse();
    final ResponseEntity<RestResponse> responseEntity = ResponseEntity.ok(restResponse);
    when(restTemplate.getForEntity(anyString(), Mockito.<Class<RestResponse>> any())).thenReturn(responseEntity);
    createJsoupMocks();

    final List<String> jsLibraries = searchHandler.handleSearchAndGetTop5Libraries("capital");
    assertNotNull(jsLibraries);
    assertEquals(5, jsLibraries.size());
    assertTrue(jsLibraries.contains("jquery.js"));
    assertTrue(jsLibraries.contains("bootstrap.js"));
  }

  private void createJsoupMocks() {
    final ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);

    doAnswer(invocationOnMock -> {
      final String value = argumentCaptor.getValue();
      final Document document = mock(Document.class);
      final Elements elements;

      switch(value)  {
        case "http://www.capital.com":

          final Element element1 = new Element(Tag.valueOf("script"), "");
          element1.attr("type", "text/javascript");
          element1.attr("src", "/test/testing/jquery.js?ver=1.4.3"); //To test Duplication

          final Element element2 = new Element(Tag.valueOf("script"), "");
          element2.attr("type", "text/javascript");
          element2.attr("src", "/test/testing/react.js");

          elements = new Elements(element1, element2);
          when(document.getElementsByTag("script")).thenReturn(elements);
          break;

        case "http://www.capitalcities.com":
          final Element element3 = new Element(Tag.valueOf("script"), "");
          element3.attr("type", "text/javascript");
          element3.attr("src", "/test/testing/bootstrap.js");

          final Element element4 = new Element(Tag.valueOf("script"), "");
          element4.attr("type", "text/javascript");
          element4.attr("src", "/test/testing/load.js");

          elements = new Elements(element3, element4);
          when(document.getElementsByTag("script")).thenReturn(elements);
          break;

        case "http://www.capitalmoney.com":
          final Element element5 = new Element(Tag.valueOf("script"), "");
          element5.attr("type", "text/javascript");
          element5.attr("src", "/test/testing/bootstrap.js");

          final Element element6 = new Element(Tag.valueOf("script"), "");
          element6.attr("type", "text/javascript");
          element6.attr("src", "/test/testing/drupal.js");

          final Element element7 = new Element(Tag.valueOf("script"), "");
          element7.attr("type", "text/javascript");
          element7.attr("src", "/test/testing/app.js");

          final Element element8 = new Element(Tag.valueOf("script"), "");
          element8.attr("type", "text/javascript");
          element8.attr("src", "/test/testing/jquery.min.js"); // To test Duplication

          final Element element9 = new Element(Tag.valueOf("script"), "");
          element9.attr("type", "text/javascript");
          element9.attr("src", "/test/testing/googleanalytics.js");

          elements = new Elements(element5, element6, element7, element8, element9);
          when(document.getElementsByTag("script")).thenReturn(elements);
          break;
      }
      return Optional.of(document);
    }).when(jsoupCrawler).getPage(argumentCaptor.capture());
  }

  private RestResponse createDummyRestResponse() {

    final RestResponse restResponse = new RestResponse();
    final Item item1 = new Item(); item1.setLink("http://www.capital.com");
    final Item item2 = new Item(); item2.setLink("http://www.capitalcities.com");
    final Item item3 = new Item(); item3.setLink("http://www.capitalmoney.com");

    final List<Item> items = Lists.newArrayList(item1, item2, item3);
    restResponse.setItems(items);

    return restResponse;
  }
}