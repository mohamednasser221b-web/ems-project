import { useState, FormEvent } from "react";
import { Box, Button, TextField, Typography, Alert } from "@mui/material";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

export function LoginPage() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const { login } = useAuth();
  const navigate = useNavigate();

  async function handleSubmit(e: FormEvent) {
    e.preventDefault();
    setError(null);
    try {
      await login(email, password);
      navigate("/employees");
    } catch {
      // Same generic message regardless of failure reason - don't help an
      // attacker distinguish "wrong password" from "account doesn't exist".
      setError("Invalid email or password");
    }
  }

  return (
    <Box component="form" onSubmit={handleSubmit} maxWidth={360} mx="auto" mt={8}>
      <Typography variant="h5" mb={2}>Employee Management System</Typography>
      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
      <TextField
        label="Email" fullWidth margin="normal" value={email}
        onChange={(e) => setEmail(e.target.value)} autoComplete="username"
      />
      <TextField
        label="Password" type="password" fullWidth margin="normal" value={password}
        onChange={(e) => setPassword(e.target.value)} autoComplete="current-password"
      />
      <Button type="submit" variant="contained" fullWidth sx={{ mt: 2 }}>
        Log in
      </Button>
    </Box>
  );
}
