package net.wuxianjie.web.page;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import net.wuxianjie.myspringbootstarter.page.PageRequest;
import net.wuxianjie.myspringbootstarter.page.PageResponse;

@RestController
@RequestMapping("/api/v1/public")
public class PageController {

    private final RestClient restClient;

    public PageController(RestClient.Builder builder) {
        this.restClient = builder.build();
    }

    @GetMapping("/test")
    public FakeData test(@Valid PageRequest pageRequest) {
        return new FakeData(
            pageRequest,
            new Date(), LocalDateTime.now(), LocalDate.now()
        );
    }

    @GetMapping("/pages")
    public PageResponse<FakeData> getPages(@Valid PageRequest pageRequest) {
        ResponseEntity<FakeData> response = restClient.get()
            .uri("http://localhost:8088/api/v1/public/test", builder -> {
                builder.queryParam("offset", pageRequest.getOffset());
                builder.queryParam("limit", pageRequest.getLimit());
                Optional.ofNullable(pageRequest.getSortBy())
                    .ifPresent(sortBy -> builder.queryParam("sortBy", sortBy));
                builder.queryParam("desc", pageRequest.isDesc());
                return builder.build();
            })
            .retrieve().toEntity(FakeData.class);
        return new PageResponse<>(
            List.of(Objects.requireNonNull(response.getBody())),
            1001, pageRequest.getOffset(), pageRequest.getLimit()
        );
    }
}
