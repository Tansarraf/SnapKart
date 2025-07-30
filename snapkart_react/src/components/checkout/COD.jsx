import { Alert, AlertTitle } from "@mui/material";

const COD = () => {
  return (
    <div className="h-96 flex justify-center items-center">
      <Alert severity="warning" variant="filled" style={{ maxWidth: "400px" }}>
        <AlertTitle>Cash on Delivery Unavailable</AlertTitle>
        Cash on Delivery is not possible for your entered location. Please use
        another method for paying.
      </Alert>
    </div>
  );
};

export default COD;
