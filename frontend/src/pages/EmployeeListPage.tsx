import { useEffect, useState } from "react";
import {
  Table, TableBody, TableCell, TableHead, TableRow,
  TextField, Pagination, Box, Typography
} from "@mui/material";
import { apiClient } from "../api/client";
import { useAuth } from "../context/AuthContext";

interface Employee {
  id: string;
  fullName: string;
  email: string;
  departmentName: string;
  salary: number | null;
  hireDate: string;
}

interface PageResponse {
  content: Employee[];
  page: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
}

export function EmployeeListPage() {
  const { role } = useAuth();
  const [data, setData] = useState<PageResponse | null>(null);
  const [page, setPage] = useState(0);
  const [search, setSearch] = useState("");

  useEffect(() => {
    apiClient
      .get<PageResponse>("/employees", { params: { page, size: 10, search: search || undefined } })
      .then((res) => setData(res.data));
  }, [page, search]);

  return (
    <Box p={4}>
      <Typography variant="h5" mb={2}>Employees</Typography>
      <TextField
        label="Search by name" value={search}
        onChange={(e) => { setSearch(e.target.value); setPage(0); }}
        sx={{ mb: 2 }}
      />
      <Table>
        <TableHead>
          <TableRow>
            <TableCell>Name</TableCell>
            <TableCell>Department</TableCell>
            <TableCell>Hire Date</TableCell>
            {role === "HR_ADMIN" && <TableCell>Salary</TableCell>}
          </TableRow>
        </TableHead>
        <TableBody>
          {data?.content.map((emp) => (
            <TableRow key={emp.id}>
              <TableCell>{emp.fullName}</TableCell>
              <TableCell>{emp.departmentName}</TableCell>
              <TableCell>{emp.hireDate}</TableCell>
              {role === "HR_ADMIN" && <TableCell>{emp.salary}</TableCell>}
            </TableRow>
          ))}
        </TableBody>
      </Table>
      {data && (
        <Pagination
          sx={{ mt: 2 }}
          count={data.totalPages}
          page={page + 1}
          onChange={(_, p) => setPage(p - 1)}
        />
      )}
    </Box>
  );
}
