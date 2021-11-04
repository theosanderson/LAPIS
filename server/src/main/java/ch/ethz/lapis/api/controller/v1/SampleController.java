package ch.ethz.lapis.api.controller.v1;

import ch.ethz.lapis.LapisMain;
import ch.ethz.lapis.api.CacheService;
import ch.ethz.lapis.api.DataVersionService;
import ch.ethz.lapis.api.SampleService;
import ch.ethz.lapis.api.entity.ApiCacheKey;
import ch.ethz.lapis.api.entity.OpennessLevel;
import ch.ethz.lapis.api.entity.SequenceType;
import ch.ethz.lapis.api.entity.req.MutationRequest;
import ch.ethz.lapis.api.entity.req.SampleAggregatedRequest;
import ch.ethz.lapis.api.entity.req.SampleDetailRequest;
import ch.ethz.lapis.api.entity.res.*;
import ch.ethz.lapis.api.exception.ForbiddenException;
import ch.ethz.lapis.api.exception.GisaidLimitationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;


@RestController
@RequestMapping("/v1/sample")
public class SampleController {

    private final Optional<CacheService> cacheServiceOpt;
    private final SampleService sampleService;
    private final DataVersionService dataVersionService;
    private final ObjectMapper objectMapper;
    private final OpennessLevel openness = LapisMain.globalConfig.getApiOpennessLevel();


    public SampleController(
        Optional<CacheService> cacheServiceOpt,
        SampleService sampleService,
        DataVersionService dataVersionService,
        ObjectMapper objectMapper
    ) {
        this.cacheServiceOpt = cacheServiceOpt;
        this.sampleService = sampleService;
        this.dataVersionService = dataVersionService;
        this.objectMapper = objectMapper;
    }


    @GetMapping(
        value = "/aggregated",
        produces = "application/json"
    )
    public ResponseEntity<String> getAggregated(SampleAggregatedRequest request) {
        ApiCacheKey cacheKey = new ApiCacheKey(CacheService.SupportedEndpoints.SAMPLE_AGGREGATED, request);
        String body = useCacheOrCompute(cacheKey, () -> {
            try {
                List<SampleAggregated> aggregatedSamples = sampleService.getAggregatedSamples(request);
                V1Response<SampleAggregatedResponse> response = new V1Response<>(new SampleAggregatedResponse(
                    request.getFields(),
                    aggregatedSamples
                ), dataVersionService.getVersion());
                return objectMapper.writeValueAsString(response);
            } catch (SQLException | JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
        return respondWithEtag(body, cacheKey);
    }


    @GetMapping("/details")
    public V1Response<SampleDetailResponse> getDetails(SampleDetailRequest request) throws SQLException {
        if (openness == OpennessLevel.GISAID) {
            throw new GisaidLimitationException();
        }
        List<SampleDetail> samples = sampleService.getDetailedSamples(request);
        return new V1Response<>(new SampleDetailResponse(samples), dataVersionService.getVersion());
    }


    @GetMapping("/contributors")
    public V1Response<ContributorResponse> getContributors(SampleDetailRequest request) throws SQLException {
        if (request.getAgeFrom() != null
            || request.getAgeTo() != null
            || request.getSex() != null
            || request.getHospitalized() != null
            || request.getDied() != null
            || request.getFullyVaccinated() != null
        ) {
            throw new ForbiddenException();
        }
        List<Contributor> contributors = sampleService.getContributors(request);
        return new V1Response<>(new ContributorResponse(contributors), dataVersionService.getVersion());
    }


    @GetMapping(
        value = "/aa-mutations",
        produces = "application/json"
    )
    public ResponseEntity<String> getAAMutations(MutationRequest request) {
        if (openness == OpennessLevel.GISAID && (
            request.getGisaidEpiIsl() != null
                || request.getGenbankAccession() != null
                || request.getSraAccession() != null
        )) {
            throw new GisaidLimitationException();
        }
        ApiCacheKey cacheKey = new ApiCacheKey(CacheService.SupportedEndpoints.SAMPLE_AA_MUTATIONS, request);
        String body = useCacheOrCompute(cacheKey, () -> {
            try {
                SampleMutationsResponse mutationsResponse = sampleService.getMutations(request,
                    SequenceType.AMINO_ACID, request.getMinProportion());
                V1Response<SampleMutationsResponse> response = new V1Response<>(mutationsResponse,
                    dataVersionService.getVersion());
                return objectMapper.writeValueAsString(response);
            } catch (SQLException | JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
        return respondWithEtag(body, cacheKey);
    }


    @GetMapping(
        value = "/nuc-mutations",
        produces = "application/json"
    )
    public ResponseEntity<String> getNucMutations(MutationRequest request) {
        if (openness == OpennessLevel.GISAID && (
            request.getGisaidEpiIsl() != null
                || request.getGenbankAccession() != null
                || request.getSraAccession() != null
        )) {
            throw new GisaidLimitationException();
        }
        ApiCacheKey cacheKey = new ApiCacheKey(CacheService.SupportedEndpoints.SAMPLE_NUC_MUTATIONS, request);
        String body = useCacheOrCompute(cacheKey, () -> {
            try {
                SampleMutationsResponse mutationsResponse = sampleService.getMutations(request,
                    SequenceType.NUCLEOTIDE, request.getMinProportion());
                V1Response<SampleMutationsResponse> response = new V1Response<>(mutationsResponse,
                    dataVersionService.getVersion());
                return objectMapper.writeValueAsString(response);
            } catch (SQLException | JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
        return respondWithEtag(body, cacheKey);
    }


    @GetMapping(
        value = "/fasta",
        produces = "text/x-fasta"
    )
    public String getFasta(SampleDetailRequest request) throws SQLException {
        if (openness == OpennessLevel.GISAID) {
            throw new GisaidLimitationException();
        }
        return sampleService.getFasta(request, false);
    }


    @GetMapping(
        value = "/fasta-aligned",
        produces = "text/x-fasta"
    )
    public String getAlignedFasta(SampleDetailRequest request) throws SQLException {
        if (openness == OpennessLevel.GISAID) {
            throw new GisaidLimitationException();
        }
        return sampleService.getFasta(request, true);
    }


    private String useCacheOrCompute(ApiCacheKey cacheKey, Supplier<String> compute) {
        if (cacheServiceOpt.isEmpty()) {
            return compute.get();
        }
        CacheService cacheService = cacheServiceOpt.get();
        String cached = cacheService.getCompressedString(cacheKey);
        if (cached != null) {
            System.out.println("Cache hit");
            return cached;
        }
        String response = compute.get();
        System.out.println("Cache miss");
        cacheService.setCompressedString(cacheKey, response);
        return response;
    }

    private <T> ResponseEntity<T> respondWithEtag(T body, ApiCacheKey cacheKey) {
        try {
            String cacheKeyString = objectMapper.writeValueAsString(cacheKey);
            String cacheKeyHash = DigestUtils.md5DigestAsHex(cacheKeyString.getBytes(StandardCharsets.UTF_8));
            String etag = cacheKeyHash + "-" + dataVersionService.getVersion();
            return ResponseEntity.ok()
                .eTag(etag)
                .body(body);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}