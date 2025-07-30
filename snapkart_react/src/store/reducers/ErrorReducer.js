const initialState = {
  isLoading: false,
  errorMessage: null,
  categoryLoader: false,
  categoryError: false,
  buttonLoader: false,
};

export const errorReducer = (state = initialState, action) => {
  switch (action.type) {
    case "IS_FETCHING":
      return {
        ...state,
        isLoading: true,
        errorMessage: null,
      };
    case "IS_SUCCESS":
      return {
        ...state,
        isLoading: false,
        errorMessage: null,
        buttonLoader: false,
        categoryLoader: false,
        categoreError: null
      };
    case "IS_ERROR":
      return {
        ...state,
        isLoading: false,
        errorMessage: action.payload,
        buttonLoader: false,
        categoryLoader: false,
      };
    case "CATEGORY_SUCCESS":
      return {
        ...state,
        categoryLoader: false,
        categoryError: null,
      };
    case "CATEGORY_LOADER":
      return {
        ...state,
        categoryLoader: true,
        categoryError: null,
        errorMessage: null,
      };
    case "BUTTON_LOADER":
      return {
        ...state,
        buttonLoader: true,
        errorMessage: null,
        categoryError: null,
      };
    default:
      return state;
  }
};
