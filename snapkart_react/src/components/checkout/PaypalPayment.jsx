import { Alert, AlertTitle } from "@mui/material";

const PaypalPayment = () => {
  return (
    <div className="h-96 flex justify-center items-center">
      <Alert severity="warning" variant="filled" style={{ maxWidth: "400px" }}>
        <AlertTitle>PayPal Unavailable</AlertTitle>
        PayPal payments not active yet. Please use another method for paying.
      </Alert>
    </div>
  );
};

export default PaypalPayment;
