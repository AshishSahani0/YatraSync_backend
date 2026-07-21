# Implementation Plan - Destination Rating Filtering

This plan outlines the changes to support dynamic star rating filters across both the Java backend query engine and the React frontend search sidebar.

## Proposed Changes

### [Backend Components]

#### [MODIFY] [DestinationFilterRequest.java](file:///d:/GitHub/YatraSync/backend/src/main/java/com/example/backend/destinations/getDestination/DestinationFilterRequest.java)
- Add field: `private Double minRating;`

#### [MODIFY] [GetDestinationService.java](file:///d:/GitHub/YatraSync/backend/src/main/java/com/example/backend/destinations/getDestination/GetDestinationService.java)
- In `getFilteredDestinations()`, add index-backed criteria matching:
  ```java
  if (filter.getMinRating() != null && filter.getMinRating() > 0.0) {
      query.addCriteria(Criteria.where("averageRating").gte(filter.getMinRating()));
  }
  ```

#### [MODIFY] [PublicDestinationController.java](file:///d:/GitHub/YatraSync/backend/src/main/java/com/example/backend/destinations/controller/PublicDestinationController.java)
- Expose `@RequestParam(required = false) Double minRating` in the public filter endpoint `getPublicDestinations(...)`.
- Forward `minRating` to the constructed `DestinationFilterRequest` builder.

---

### [Frontend Components]

#### [MODIFY] [publicApi.ts](file:///d:/GitHub/YatraSync/frontend/src/api/publicApi.ts)
- Update `GetDestinationsParams` interface to include `minRating?: number`.
- Append `minRating` parameter value to the endpoint URL query string builder inside `getPublicDestinations(...)`.

#### [MODIFY] [DestinationQuery.tsx](file:///d:/GitHub/YatraSync/frontend/src/pages/destinationquery/DestinationQuery.tsx)
- Define a filter state: `const [minRating, setMinRating] = useState<number | "ALL">("ALL");`
- Pass `minRating: minRating === "ALL" ? undefined : minRating` inside the `getPublicDestinations` load parameters.
- Render a premium ratings filter sidebar control beneath the budget selector, allowing users to filter by **4.0+ Stars, 3.0+ Stars, or 2.0+ Stars** dynamically.
- Clear filters handler resets `minRating` to `"ALL"`.

---

## Verification Plan

### Automated Tests
1. **Compiles successfully**: Check both frontend and backend compilation.
2. **API rating filter test**: Query `/api/public/destinations?minRating=4.0` and verify it returns only destinations with rating >= 4.0.
3. **Sidebar toggle check**: Test checking "⭐ 4.0 & Up" in the frontend sidebar, and verify it updates the results grid immediately.
